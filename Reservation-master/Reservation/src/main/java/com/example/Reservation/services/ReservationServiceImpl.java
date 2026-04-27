package com.example.Reservation.services;

import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.dtos.RevenueResponseDto;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.exceptions.GuestNotFoundException;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.ReservationMapper;
import com.example.Reservation.repositories.CancellationRepository;
import com.example.Reservation.repositories.GuestRepository;
import com.example.Reservation.repositories.PaymentRepository;
import com.example.Reservation.repositories.ReservationRepository;
import com.example.Reservation.specifications.ReservationSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;

    private final EmailService emailService;

    private final GuestRepository guestRepository;

    private final PaymentRepository paymentRepository;

    private final CancellationRepository cancellationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, EmailService emailService, GuestRepository guestRepository, PaymentRepository paymentRepository, CancellationRepository cancellationRepository) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.guestRepository = guestRepository;
        this.paymentRepository = paymentRepository;
        this.cancellationRepository = cancellationRepository;
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

        reservation.setStatus(ReservationStatus.CONFIRMED);

        Guest guest= reservation.getGuest();
        int newPoints=calculateLoyaltyPoints(reservation);
        guest.setLoyaltyPoints(newPoints);
        guestRepository.save(guest);

        reservationRepository.save(reservation);

        emailService.sendMail(reservation);

        return ReservationMapper.toResponseDto(reservation);
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

    private boolean isValidReservation(LocalDate arrival,LocalDate departure){
        return arrival.isBefore(departure) || !arrival.isBefore(LocalDate.now());
    }

    private int calculateLoyaltyPoints(Reservation reservation){

        int days= (int)ChronoUnit.DAYS.between(reservation.getArrivalDate(),reservation.getDepartureDate());

        return reservation.getGuest().getLoyaltyPoints()+days*10;

    }
}
