package com.example.Reservation.services;

import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.ReservationMapper;
import com.example.Reservation.repositories.ReservationRepository;
import com.example.Reservation.specifications.ReservationSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;

    private final EmailService emailService;

    public ReservationServiceImpl(ReservationRepository reservationRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    @Override
    public ReservationResponse addReservation(ReservationRequest request) {

        if(!isValidReservation(request.getArrivalDate(),request.getDepartureDate()))
            throw new RuntimeException("Select appropriate dates of booking.");

        Reservation reservation= ReservationMapper.toEntity(request);

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

    private boolean isValidReservation(LocalDate arrival,LocalDate departure){
        return arrival.isBefore(departure) || !arrival.isBefore(LocalDate.now());
    }
}
