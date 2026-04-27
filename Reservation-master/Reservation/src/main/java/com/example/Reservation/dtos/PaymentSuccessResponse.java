package com.example.Reservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessResponse {

    private Double totalAmountPaid;

    private Integer remainingLoyaltyPoints;
}
