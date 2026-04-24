package com.example.Reservation.services;

import com.example.Reservation.dtos.PaymentResponse;

import java.util.List;

public interface PaymentService {

    void pay(Long reservationId,Double amount);

    List<PaymentResponse> getAllPayments(Long reservationId);
}
