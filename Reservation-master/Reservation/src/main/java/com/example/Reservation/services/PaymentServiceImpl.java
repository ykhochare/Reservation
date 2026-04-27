package com.example.Reservation.services;

import com.example.Reservation.dtos.PaymentResponse;
import com.example.Reservation.dtos.PaymentSuccessResponse;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.Payment;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.PaymentMapper;
import com.example.Reservation.repositories.GuestRepository;
import com.example.Reservation.repositories.PaymentRepository;
import com.example.Reservation.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    private final ReservationRepository reservationRepository;

    private final GuestRepository guestRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, ReservationRepository reservationRepository, GuestRepository guestRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
    }

    @Override
    @Transactional
    public PaymentSuccessResponse pay(Long reservationId, Double amount,Integer pointsUseTo) {

        Reservation reservation=reservationRepository.findById(reservationId).orElseThrow(()->new ReservationNotFoundException("Reservation not found..."));

        List<Payment> payments=reservation.getPayments();

        double paidAmount=payments.stream().mapToDouble(Payment::getAmount).sum();

        double totalPaidAmount=paidAmount+amount;

        if(totalPaidAmount>reservation.getTotalAmount())
            throw new RuntimeException("Payment exceeds");

        Payment payment=new Payment();

        payment.setAmount(amount);
        payment.setReservation(reservation);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        Guest guest= reservation.getGuest();
        int updatedLoyaltyPoints=guest.getLoyaltyPoints()-pointsUseTo;
        guest.setLoyaltyPoints(updatedLoyaltyPoints);
        guestRepository.save(guest);

        return new PaymentSuccessResponse(totalPaidAmount,updatedLoyaltyPoints);

    }

    @Override
    public List<PaymentResponse> getAllPayments(Long reservationId) {

        List<Payment> payments=paymentRepository.getByReservation_Id(reservationId);

        return payments.stream().map(PaymentMapper::toResponseDto).toList();
    }
}
