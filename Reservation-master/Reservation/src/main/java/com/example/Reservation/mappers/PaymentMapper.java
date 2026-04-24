package com.example.Reservation.mappers;

import com.example.Reservation.dtos.PaymentResponse;
import com.example.Reservation.entities.Payment;

public class PaymentMapper {

    public static PaymentResponse toResponseDto(Payment payment){

        PaymentResponse response=new PaymentResponse();
        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setDateTime(payment.getPaymentDate());

        return response;
    }
}
