package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellationPolicyRequest {

    private String name;

    private Integer daysBeforeCheckInFrom;

    private Integer daysBeforeCheckInTo;

    private Double refundPercentage;
}
