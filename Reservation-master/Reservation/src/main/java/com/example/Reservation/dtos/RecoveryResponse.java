package com.example.Reservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecoveryResponse {
    private Double totalAmountToRecover;

    private Integer totalCount;

    private List<AgentCommissionResponse> commissions;
}
