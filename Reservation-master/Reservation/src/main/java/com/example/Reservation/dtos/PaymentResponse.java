package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long id;

    private Double amount;

    private LocalDateTime dateTime;
}
