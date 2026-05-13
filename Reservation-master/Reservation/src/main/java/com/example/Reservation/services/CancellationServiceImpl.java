package com.example.Reservation.services;


import com.example.Reservation.dtos.CancellationResponseDto;
import com.example.Reservation.entities.*;
import com.example.Reservation.enums.*;
import com.example.Reservation.events.ReservationCancelledEvent;
import com.example.Reservation.exceptions.CancellationNotFoundException;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.CancellationMapper;
import com.example.Reservation.repositories.*;
import com.example.Reservation.specifications.CancellationSpecification;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CancellationServiceImpl implements CancellationService{

    private final CancellationPolicyRepository cancellationPolicyRepository;

    private final CancellationRepository cancellationRepository;

    private final ReservationRepository reservationRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final BungalowAvailabilityRepository bungalowAvailabilityRepository;

    public CancellationServiceImpl(CancellationPolicyRepository cancellationPolicyRepository,
                                   CancellationRepository cancellationRepository,
                                   ReservationRepository reservationRepository,
                                   ApplicationEventPublisher eventPublisher,
                                   BungalowAvailabilityRepository bungalowAvailabilityRepository) {
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.cancellationRepository = cancellationRepository;
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
        this.bungalowAvailabilityRepository = bungalowAvailabilityRepository;
    }

    @Override
    @Transactional
    public double cancelReservation(Long reservationId) {

        Reservation reservation=reservationRepository.findById(reservationId).orElseThrow(()->new ReservationNotFoundException("Reservation not found..."));

        if(reservation.getStatus()!=ReservationStatus.CONFIRMED){
            throw new RuntimeException("Only confirmed reservations can be cancel");
        }

        int days= (int)ChronoUnit.DAYS.between(LocalDate.now(),reservation.getArrivalDate());

        CancellationPolicy policy=cancellationPolicyRepository.findByDaysBeforeCheckIn(days);

        double refundAmount=policy.getRefundPercentage()*reservation.getTotalAmount()*0.01;

        Cancellation cancellation=new Cancellation();
        cancellation.setReason("Cancelled by guest.");
        cancellation.setReservation(reservation);
        cancellation.setRefundAmount(refundAmount);
        cancellation.setPolicy(policy);
        cancellation.setRefundStatus(RefundStatus.PENDING);
        cancellation.setDaysBeforeCheckIn(days);

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        cancellationRepository.save(cancellation);

        merge(reservation.getBungalow().getBungalowId(),reservation.getArrivalDate(),reservation.getDepartureDate());

        eventPublisher.publishEvent(new ReservationCancelledEvent(reservation,refundAmount));

        return refundAmount;
    }

    @Override
    public List<CancellationResponseDto> allCancellations(RefundStatus status,Long policyId) {

        Specification<Cancellation> spec=Specification.where(null);

        if(status!=null)
            spec= spec.and(CancellationSpecification.hasStatus(status));

        if(policyId!=null)
            spec=spec.and(CancellationSpecification.hasPolicyId(policyId));

        List<Cancellation> cancellations=cancellationRepository.findAll(spec);

        return cancellations.stream().map(CancellationMapper::toResponseDto).toList();
    }

    @Override
    public CancellationResponseDto getById(Long id) {

        Cancellation cancellation=cancellationRepository.findById(id).orElseThrow(()->new CancellationNotFoundException("Cancellation not found."));

        return CancellationMapper.toResponseDto(cancellation);
    }

    @Override
    public CancellationResponseDto getByReservation(Long id) {

        Cancellation cancellation=cancellationRepository.findByReservation_Id(id).orElseThrow(()->new CancellationNotFoundException("Cancellation not found."));

        return CancellationMapper.toResponseDto(cancellation);
    }

    public void merge(Long bungalowId,LocalDate arrivalDate,LocalDate departureDate){
        BungalowAvailability availability=bungalowAvailabilityRepository.findBookedRecord(bungalowId,arrivalDate,departureDate)
                                                                                                .orElseThrow(()->new RuntimeException("Bungalow availability not found..."));

        availability.setStatus(AvailabilityStatus.AVAILABLE);


        bungalowAvailabilityRepository.findByBungalow_BungalowIdAndStatusAndToDate(bungalowId,AvailabilityStatus.AVAILABLE,arrivalDate.minusDays(1))
                                                                    .ifPresent(left ->{availability.setFromDate(left.getFromDate());
                                                                                                        bungalowAvailabilityRepository.delete(left);} );

        bungalowAvailabilityRepository.findByBungalow_BungalowIdAndStatusAndFromDate(bungalowId,AvailabilityStatus.AVAILABLE,departureDate.plusDays(1))
                                                                    .ifPresent(right -> {availability.setToDate(right.getToDate());
                                                                                                          bungalowAvailabilityRepository.delete(right);});

        bungalowAvailabilityRepository.save(availability);

    }

}
