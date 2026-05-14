package com.example.Reservation.services;

import com.example.Reservation.config.RabbitMQConfig;
import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.entities.*;
import com.example.Reservation.enums.*;
import com.example.Reservation.events.EmailEvent;
import com.example.Reservation.exceptions.BungalowNotFoundException;
import com.example.Reservation.exceptions.GuestNotFoundException;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.ReservationMapper;
import com.example.Reservation.repositories.*;
import com.example.Reservation.specifications.ReservationSpecification;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;

    private final BungalowRepository bungalowRepository;

    private final GuestRepository guestRepository;

    private final AgentCommissionRepository agentCommissionRepository;

    private final TravelAgentRepository travelAgentRepository;

    private final BungalowAvailabilityRepository bungalowAvailabilityRepository;

    private final LoyaltyPointsService loyaltyPointsService;

    private final RabbitTemplate rabbitTemplate;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  BungalowRepository bungalowRepository,
                                  GuestRepository guestRepository,
                                  AgentCommissionRepository agentCommissionRepository,
                                  TravelAgentRepository travelAgentRepository,
                                  BungalowAvailabilityRepository bungalowAvailabilityRepository, LoyaltyPointsService loyaltyPointsService,
                                  RabbitTemplate rabbitTemplate) {
        this.reservationRepository = reservationRepository;
        this.bungalowRepository = bungalowRepository;
        this.guestRepository = guestRepository;
        this.agentCommissionRepository = agentCommissionRepository;
        this.travelAgentRepository = travelAgentRepository;
        this.bungalowAvailabilityRepository = bungalowAvailabilityRepository;
        this.loyaltyPointsService = loyaltyPointsService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ReservationResponse addReservation(ReservationRequest request,Long bungalowId) {

        Guest guest=guestRepository.findById(request.getGuestId()).orElseThrow(()->new GuestNotFoundException("Guest Not Found..."));

        Bungalow bungalow=bungalowRepository.findById(bungalowId).orElseThrow(()->new BungalowNotFoundException("Bungalow not found..."));

        if(!isValidReservation(request.getArrivalDate(),request.getDepartureDate()))
            throw new RuntimeException("Select appropriate dates of booking.");

        Reservation reservation= ReservationMapper.toEntity(request);
        reservation.setGuest(guest);
        reservation.setBungalow(bungalow);

        if(reservationRepository.existsOverlap(bungalowId,ReservationStatus.CONFIRMED,request.getArrivalDate(),request.getDepartureDate())){
            reservation.setStatus(ReservationStatus.WAITING);
        }else{
            reservation.setStatus(ReservationStatus.PENDING);
        }

        int days=(int)ChronoUnit.DAYS.between(request.getArrivalDate(),request.getDepartureDate());
        double totalAmount=days * bungalow.getPricePerNight();
        reservation.setTotalAmount(totalAmount);

        Reservation savedReservation=reservationRepository.save(reservation);

        return ReservationMapper.toResponseDto(savedReservation);
    }

    @Override
    public ReservationResponse confirmBooking(Long id) {
        Reservation reservation=reservationRepository.findById(id).orElseThrow(()->new ReservationNotFoundException("Reservation not found..."));

        if(reservation.getStatus()!=ReservationStatus.PENDING)
            throw new RuntimeException("Only pending reservations can be confirmed");

        boolean isOverlapping=reservationRepository.existsOverlap(reservation.getBungalow().getBungalowId(),ReservationStatus.CONFIRMED,reservation.getArrivalDate(),reservation.getDepartureDate());

        if(isOverlapping){
            reservation.setStatus(ReservationStatus.WAITING);
            reservationRepository.save(reservation);
            throw new RuntimeException("Bungalow already confirmed for these dates.Reservation moved to WAITING.");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);

        Guest guest= reservation.getGuest();
        int newPoints=loyaltyPointsService.calculateConfirmationLoyaltyPoints(reservation);

        loyaltyPointsService.saveLoyaltyPointsHistory(guest,newPoints,PointsType.EARNED);
        guest.setLoyaltyPoints(guest.getLoyaltyPoints()+newPoints);
        guest.setTotalPointsEarned(guest.getTotalPointsEarned()+newPoints);
        loyaltyPointsService.updateLoyaltyTier(guest);
        guestRepository.save(guest);

        reservationRepository.save(reservation);

        split(reservation.getBungalow(),reservation.getArrivalDate(),reservation.getDepartureDate());

        ReservationResponse response=ReservationMapper.toResponseDto(reservation);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                new EmailEvent(response.getGuestEmail(),"Reservation Confirmed",confirmationEmailBody(reservation))
        );

        return response;
    }

    @Override
    public List<ReservationResponse> getAllReservations(ReservationStatus status, LocalDate arrivalDate, LocalDate departureDate, Long bungalowId) {

        Specification<Reservation> spec=Specification.where(null);

        if(status!=null)
            spec=spec.and(ReservationSpecification.hasStatus(status));

        if(arrivalDate!=null)
            spec=spec.and(ReservationSpecification.hasArrival(arrivalDate));

        if(departureDate!=null)
            spec=spec.and(ReservationSpecification.hasDeparture(departureDate));

        if(bungalowId!=null)
            spec=spec.and(ReservationSpecification.hasBungalow(bungalowId));

        List<Reservation> reservations=reservationRepository.findAll(spec);

        return reservations.stream().map(ReservationMapper::toResponseDto).toList();
    }

    @Override
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation=reservationRepository.findById(id).orElseThrow(()->new ReservationNotFoundException("Reservation Not found.."));

        return ReservationMapper.toResponseDto(reservation);
    }


    @Override
    public int confirmAgentReservations(Long agentId) {

        List<Reservation> agentReservations=reservationRepository.findByTravelAgentAgentIdAndStatus(agentId,ReservationStatus.PENDING);

        TravelAgent agent=travelAgentRepository.findById(agentId).orElseThrow(()->new RuntimeException("Agent not found..."));

        for(Reservation reservation:agentReservations){
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);

            double commissionAmount=reservation.getTotalAmount() * agent.getCommissionRate() * 0.01;

            AgentCommission commission=new AgentCommission();
            commission.setReservation(reservation);
            commission.setTravelAgent(agent);
            commission.setCommissionAmount(commissionAmount);
            commission.setStatus(CommissionStatus.PENDING);
            agentCommissionRepository.save(commission);

        }

        return agentReservations.size();
    }

    @Override
    @Transactional
    public ReservationResponse checkOut(Long reservationId) {

        Reservation reservation=reservationRepository.findById(reservationId).orElseThrow(()->new ReservationNotFoundException("Reservation not found"));

        List<Payment> payments=reservation.getPayments();

        Double totalPaidAmount=payments.stream().mapToDouble(Payment::getAmount).sum();

        if(totalPaidAmount<reservation.getTotalAmount())
            throw new RuntimeException("Cannot checkout. Payment pending! " + "Paid: ₹" + totalPaidAmount + " Remaining: ₹" + (reservation.getTotalAmount() - totalPaidAmount));

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        AgentCommission commission=reservation.getAgentCommission();

        if(commission!=null){
            commission.setStatus(CommissionStatus.PAID);
            commission.setPaidAt(LocalDateTime.now());
            agentCommissionRepository.save(commission);
        }

        return ReservationMapper.toResponseDto(reservation);
    }

    private boolean isValidReservation(LocalDate arrival,LocalDate departure){
        return arrival.isBefore(departure) || !arrival.isBefore(LocalDate.now());
    }

    public void split(Bungalow bungalow,LocalDate arrivalDate,LocalDate departureDate){
        BungalowAvailability availability=bungalowAvailabilityRepository.findCoveringAvailableRecord(bungalow.getBungalowId(), arrivalDate, departureDate)
                                                                                         .orElseThrow(()->new RuntimeException("Bungalow is not available..."));

        List<BungalowAvailability> toSave=new ArrayList<>();

        if(arrivalDate.isAfter(availability.getFromDate())){
            BungalowAvailability leftAvailable=new BungalowAvailability();
            leftAvailable.setBungalow(bungalow);
            leftAvailable.setStatus(AvailabilityStatus.AVAILABLE);
            leftAvailable.setFromDate(availability.getFromDate());
            leftAvailable.setToDate(arrivalDate.minusDays(1));
            toSave.add(leftAvailable);
        }

        BungalowAvailability bungalowAvailability=new BungalowAvailability();
        bungalowAvailability.setBungalow(bungalow);
        bungalowAvailability.setStatus(AvailabilityStatus.BOOKED);
        bungalowAvailability.setFromDate(arrivalDate);
        bungalowAvailability.setToDate(departureDate);
        toSave.add(bungalowAvailability);

        if(departureDate.isBefore(availability.getToDate())){
            BungalowAvailability rightAvailable=new BungalowAvailability();
            rightAvailable.setBungalow(bungalow);
            rightAvailable.setStatus(AvailabilityStatus.AVAILABLE);
            rightAvailable.setFromDate(departureDate.plusDays(1));
            rightAvailable.setToDate(availability.getToDate());
            toSave.add(rightAvailable);
        }

        bungalowAvailabilityRepository.saveAll(toSave);

        bungalowAvailabilityRepository.delete(availability);

    }

    private String confirmationEmailBody(Reservation reservation){
        return "Dear " + reservation.getGuest().getGuestName() + ",\n\n" +
                "Your reservation has been confirmed at Silver Heavens Resort!\n\n" +
                "Reservation Details:\n" +
                "Bungalow ID  : " + reservation.getBungalow().getBungalowId() + "\n" +
                "Arrival Date : " + reservation.getArrivalDate() + "\n" +
                "Departure Date: " + reservation.getDepartureDate() + "\n" +
                "Total Amount : ₹" + reservation.getTotalAmount() + "\n\n" +
                "We look forward to welcoming you!\n\n" +
                "Regards,\n" +
                "Silver Heavens Resort";
    }

}
