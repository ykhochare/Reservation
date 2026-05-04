package com.example.Reservation.dtos;

import com.example.Reservation.enums.CommissionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentCommissionResponse {

    private Long id;

    private Long reservationId;

    private String agentName;

    private Double commissionAmount;

    private CommissionStatus status;
}
