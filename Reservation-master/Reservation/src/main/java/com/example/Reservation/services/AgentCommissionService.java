package com.example.Reservation.services;

import com.example.Reservation.dtos.AgentCommissionResponse;
import com.example.Reservation.dtos.CommissionStatement;

import java.time.YearMonth;
import java.util.List;

public interface AgentCommissionService {

    List<AgentCommissionResponse> getAllByAgentId(Long agentId);

    CommissionStatement getByAgentAndMonth(Long agentId, YearMonth yearMonth);
}
