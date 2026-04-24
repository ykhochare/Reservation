package com.example.Reservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CancellationResponse {

    private String message;

    private double refundAmount;
}
