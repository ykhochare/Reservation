package com.example.Reservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommissionStatement {
    private Long agentId;
    private String month;
    private Double totalCommissionEarned;
    private Double totalCommissionReversed;
    private Double netCommission;
}
