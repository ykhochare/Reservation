package com.example.Reservation.services;

import com.example.Reservation.dtos.PaymentResponse;
import com.example.Reservation.dtos.PaymentSuccessResponse;
import com.example.Reservation.dtos.RevenueResponseDto;

import java.util.List;

public interface PaymentService {

    PaymentSuccessResponse pay(Long reservationId, Double amount,Integer pointsUseTo);

    List<PaymentResponse> getAllPayments(Long reservationId);


}
