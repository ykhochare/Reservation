package com.example.Reservation.services;

import com.example.Reservation.dtos.AgentCommissionResponse;
import com.example.Reservation.dtos.CommissionStatement;
import com.example.Reservation.dtos.RecoveryResponse;

import java.time.YearMonth;
import java.util.List;

public interface AgentCommissionService {

    List<AgentCommissionResponse> getAllByAgentId(Long agentId);

    CommissionStatement getByAgentAndMonth(Long agentId, YearMonth yearMonth);

    RecoveryResponse getAllRecoveries();

    void payCommission(Long commissionId);
}
