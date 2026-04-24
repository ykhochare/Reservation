package com.example.Reservation.services;


import com.example.Reservation.dtos.CancellationResponseDto;
import com.example.Reservation.entities.Cancellation;
import com.example.Reservation.entities.CancellationPolicy;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.RefundStatus;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.exceptions.CancellationNotFoundException;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.CancellationMapper;
import com.example.Reservation.repositories.CancellationPolicyRepository;
import com.example.Reservation.repositories.CancellationRepository;
import com.example.Reservation.repositories.ReservationRepository;
import com.example.Reservation.specifications.CancellationSpecification;
import jakarta.transaction.Transactional;
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

    public CancellationServiceImpl(CancellationPolicyRepository cancellationPolicyRepository, CancellationRepository cancellationRepository, ReservationRepository reservationRepository) {
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.cancellationRepository = cancellationRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public double cancelReservation(Long reservationId) {

        Reservation reservation=reservationRepository.findById(reservationId).orElseThrow(()->new ReservationNotFoundException("Reservation not found..."));

        if(reservation.getStatus()== ReservationStatus.PENDING){
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            return 0.0;
        }

        if(reservation.getStatus()!=ReservationStatus.CONFIRMED){
            throw new RuntimeException("Your reservation is not confirmed.");
        }

        int days= (int)ChronoUnit.DAYS.between(LocalDate.now(),reservation.getArrivalDate());

        CancellationPolicy policy=cancellationPolicyRepository.findByDaysBeforeCheckIn(days);

        double refundAmount=policy.getRefundPercentage()*reservation.getTotalAmount()*0.01;

        Cancellation cancellation=new Cancellation();
        cancellation.setReason("Some issue.");
        cancellation.setReservation(reservation);
        cancellation.setRefundAmount(refundAmount);
        cancellation.setPolicy(policy);
        cancellation.setRefundStatus(RefundStatus.PENDING);
        cancellation.setDaysBeforeCheckIn(days);

        cancellationRepository.save(cancellation);

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        reservationRepository.findTopWaitingReservation(reservation.getBungalowId(),"WAITING")
                .ifPresent(waiting->{waiting.setStatus(ReservationStatus.CONFIRMED);
                                                reservationRepository.save(waiting);});

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


}
