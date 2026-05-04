package com.example.Reservation.mappers;

import com.example.Reservation.dtos.AgentCommissionResponse;
import com.example.Reservation.entities.AgentCommission;

public class AgentCommissionMapper {

    public static AgentCommissionResponse toResponseDTo(AgentCommission commission){
        AgentCommissionResponse response=new AgentCommissionResponse();
        response.setId(commission.getId());
        response.setReservationId(commission.getReservation().getId());
        response.setAgentName(commission.getTravelAgent().getAgentName());
        response.setCommissionAmount(commission.getCommissionAmount());
        response.setStatus(commission.getStatus());

        return response;
    }
}
