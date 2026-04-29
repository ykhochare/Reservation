package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelAgentResponse {

    private Long agentId;

    private String agentName;

    private String agentEmail;

    private Double commissionRate;
}
