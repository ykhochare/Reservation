package com.example.Reservation.services;


import com.example.Reservation.dtos.CancellationResponseDto;
import com.example.Reservation.entities.Cancellation;
import com.example.Reservation.entities.CancellationPolicy;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.RefundStatus;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.exceptions.CancellationNotFoundException;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.CancellationMapper;
import com.example.Reservation.repositories.CancellationPolicyRepository;
import com.example.Reservation.repositories.CancellationRepository;
import com.example.Reservation.repositories.GuestRepository;
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

    private final GuestRepository guestRepository;

    private final EmailService emailService;

    public CancellationServiceImpl(CancellationPolicyRepository cancellationPolicyRepository, CancellationRepository cancellationRepository, ReservationRepository reservationRepository, GuestRepository guestRepository, EmailService emailService) {
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.cancellationRepository = cancellationRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.emailService = emailService;
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
            throw new RuntimeException("Only confirmed reservations can be cancel");
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

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        cancellationRepository.save(cancellation);



        Guest guest=reservation.getGuest();
        int newPoints=Math.max(calculateCancellationLoyaltyPoints(reservation),0);
        guest.setLoyaltyPoints(newPoints);

        guestRepository.save(guest);




        reservationRepository.findTopWaitingReservation(reservation.getBungalowId(),"WAITING")
                .ifPresent(waiting->{waiting.setStatus(ReservationStatus.CONFIRMED);
                                                reservationRepository.save(waiting);
                                                emailService.sendMail(waiting);
                                                Guest waitedGuest= waiting.getGuest();
                                                int updatedPoints=calculateConfirmationLoyaltyPoints(waiting);
                                                waitedGuest.setLoyaltyPoints(updatedPoints);
                                                guestRepository.save(waitedGuest);});

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

    private int calculateCancellationLoyaltyPoints(Reservation reservation){

        int days= (int)ChronoUnit.DAYS.between(reservation.getArrivalDate(),reservation.getDepartureDate());

        return reservation.getGuest().getLoyaltyPoints()-days*10;

    }

    private int calculateConfirmationLoyaltyPoints(Reservation reservation){

        int days= (int)ChronoUnit.DAYS.between(reservation.getArrivalDate(),reservation.getDepartureDate());

        return reservation.getGuest().getLoyaltyPoints()+days*10;

    }

}
