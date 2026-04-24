package com.example.Reservation.services;

import com.example.Reservation.dtos.PaymentResponse;
import com.example.Reservation.entities.Payment;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.PaymentMapper;
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

    public PaymentServiceImpl(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public void pay(Long reservationId,Double amount) {

        Reservation reservation=reservationRepository.findById(reservationId).orElseThrow(()->new ReservationNotFoundException("Reservation not found..."));

        List<Payment> payments=reservation.getPayments();

        double paidAmount=payments.stream().mapToDouble(Payment::getAmount).sum();

        if(paidAmount+amount>reservation.getTotalAmount())
            throw new RuntimeException("Payment exceeds");

        Payment payment=new Payment();

        payment.setAmount(amount);
        payment.setReservation(reservation);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);


    }

    @Override
    public List<PaymentResponse> getAllPayments(Long reservationId) {

        List<Payment> payments=paymentRepository.getByReservation_Id(reservationId);

        return payments.stream().map(PaymentMapper::toResponseDto).toList();
    }
}
