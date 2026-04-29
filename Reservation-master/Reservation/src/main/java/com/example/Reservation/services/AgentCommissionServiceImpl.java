package com.example.Reservation.services;

import com.example.Reservation.dtos.AgentCommissionResponse;
import com.example.Reservation.dtos.CommissionStatement;
import com.example.Reservation.entities.AgentCommission;
import com.example.Reservation.enums.CommissionStatus;
import com.example.Reservation.mappers.AgentCommissionMapper;
import com.example.Reservation.repositories.AgentCommissionRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class AgentCommissionServiceImpl implements AgentCommissionService{

    private final AgentCommissionRepository agentCommissionRepository;

    public AgentCommissionServiceImpl(AgentCommissionRepository agentCommissionRepository) {
        this.agentCommissionRepository = agentCommissionRepository;
    }

    @Override
    public List<AgentCommissionResponse> getAllByAgentId(Long agentId) {
        List<AgentCommission> commissions=agentCommissionRepository.findByTravelAgentAgentId(agentId);

        return commissions.stream().map(AgentCommissionMapper::toResponseDTo).toList();
    }

    @Override
    public CommissionStatement getByAgentAndMonth(Long agentId, YearMonth yearMonth) {
        List<AgentCommission> commissions=agentCommissionRepository.findByAgentAndMonth(agentId, yearMonth.getYear(), yearMonth.getMonthValue());

        double totalCommissionEarned=commissions.stream().filter(c->c.getStatus()== CommissionStatus.PAID)
                                            .mapToDouble(AgentCommission::getCommissionAmount).sum();

        double totalCommissionReversed=commissions.stream().filter(c->c.getStatus()==CommissionStatus.REVERSED)
                                            .mapToDouble(AgentCommission::getCommissionAmount).sum();

        double netCommission=totalCommissionEarned-totalCommissionReversed;

        return new CommissionStatement(agentId,yearMonth.toString(),totalCommissionEarned,totalCommissionReversed,netCommission);


    }
}
