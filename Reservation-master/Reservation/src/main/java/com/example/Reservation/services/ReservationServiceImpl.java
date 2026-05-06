package com.example.Reservation.services;

import com.example.Reservation.config.RabbitMQConfig;
import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.dtos.RevenueResponseDto;
import com.example.Reservation.entities.*;
import com.example.Reservation.enums.CommissionStatus;
import com.example.Reservation.enums.LoyaltyTier;
import com.example.Reservation.enums.PointsType;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.events.ReservationConfirmedEvent;
import com.example.Reservation.exceptions.GuestNotFoundException;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.ReservationMapper;
import com.example.Reservation.repositories.*;
import com.example.Reservation.specifications.ReservationSpecification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;

    private final GuestRepository guestRepository;

    private final PaymentRepository paymentRepository;

    private final CancellationRepository cancellationRepository;

    private final LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository;

    private final AgentCommissionRepository agentCommissionRepository;

    private final TravelAgentRepository travelAgentRepository;

    private final RabbitTemplate rabbitTemplate;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  GuestRepository guestRepository,
                                  PaymentRepository paymentRepository,
                                  CancellationRepository cancellationRepository,
                                  LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository,
                                  AgentCommissionRepository agentCommissionRepository,
                                  TravelAgentRepository travelAgentRepository,
                                  RabbitTemplate rabbitTemplate) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.paymentRepository = paymentRepository;
        this.cancellationRepository = cancellationRepository;
        this.loyaltyPointsHistoryRepository = loyaltyPointsHistoryRepository;
        this.agentCommissionRepository = agentCommissionRepository;
        this.travelAgentRepository = travelAgentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ReservationResponse addReservation(ReservationRequest request) {

        Guest guest=guestRepository.findById(request.getGuestId()).orElseThrow(()->new GuestNotFoundException("Guest Not Found..."));

        if(!isValidReservation(request.getArrivalDate(),request.getDepartureDate()))
            throw new RuntimeException("Select appropriate dates of booking.");

        Reservation reservation= ReservationMapper.toEntity(request);
        reservation.setGuest(guest);

        if(reservationRepository.existsOverlap(request.getBungalowId(),ReservationStatus.CONFIRMED,request.getArrivalDate(),request.getDepartureDate())){
            reservation.setStatus(ReservationStatus.WAITING);
        }else{
            reservation.setStatus(ReservationStatus.PENDING);
        }

        Reservation savedReservation=reservationRepository.save(reservation);

        return ReservationMapper.toResponseDto(savedReservation);
    }

    @Override
    public ReservationResponse confirmBooking(Long id) {
        Reservation reservation=reservationRepository.findById(id).orElseThrow(()->new ReservationNotFoundException("Reservation not found..."));

        if(reservation.getStatus()!=ReservationStatus.PENDING)
            throw new RuntimeException("Only pending reservations can be confirmed");

        reservation.setStatus(ReservationStatus.CONFIRMED);

        Guest guest= reservation.getGuest();
        int newPoints=calculateLoyaltyPoints(reservation);

        saveLoyaltyPointsHistory(guest,newPoints);
        guest.setLoyaltyPoints(guest.getLoyaltyPoints()+newPoints);
        guest.setTotalPointsEarned(guest.getTotalPointsEarned()+newPoints);
        updateLoyaltyTier(guest);
        guestRepository.save(guest);

        reservationRepository.save(reservation);

        ReservationResponse response=ReservationMapper.toResponseDto(reservation);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONFIRMATION_EXCHANGE,
                RabbitMQConfig.CONFIRMATION_ROUTING_KEY,
                new ReservationConfirmedEvent(response)
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
    public RevenueResponseDto getRevenueByBungalow(Long bungalowId) {
        double totalRevenue=paymentRepository.calculateRevenueByBungalow(bungalowId);

        double totalRefunds= cancellationRepository.calculateRefundByBungalow(bungalowId);

        double netRevenue=totalRevenue-totalRefunds;
        return new RevenueResponseDto(bungalowId,totalRevenue,totalRefunds,netRevenue);
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

    private boolean isValidReservation(LocalDate arrival,LocalDate departure){
        return arrival.isBefore(departure) || !arrival.isBefore(LocalDate.now());
    }

    private int calculateLoyaltyPoints(Reservation reservation){

        int days= (int)ChronoUnit.DAYS.between(reservation.getArrivalDate(),reservation.getDepartureDate());

        return days*10;

    }

    private void saveLoyaltyPointsHistory(Guest guest, Integer points){

        LoyaltyPointsHistory history=new LoyaltyPointsHistory();
        history.setGuest(guest);
        history.setPoints(points);
        history.setPointsType(PointsType.EARNED);
        loyaltyPointsHistoryRepository.save(history);
    }

    private void updateLoyaltyTier(Guest guest) {
        int totalEarned = guest.getTotalPointsEarned();
        if (totalEarned >= 1000) {
            guest.setLoyaltyTier(LoyaltyTier.GOLD);
        } else if (totalEarned >= 500) {
            guest.setLoyaltyTier(LoyaltyTier.SILVER);
        } else {
            guest.setLoyaltyTier(LoyaltyTier.BRONZE);
        }
    }
}
