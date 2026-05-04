package com.example.Reservation.controllers;

import com.example.Reservation.dtos.AgentCommissionResponse;
import com.example.Reservation.dtos.CommissionStatement;
import com.example.Reservation.dtos.RecoveryResponse;
import com.example.Reservation.services.AgentCommissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/commissions")
public class AgentCommissionController {

    private final AgentCommissionService agentCommissionService;

    public AgentCommissionController(AgentCommissionService agentCommissionService) {
        this.agentCommissionService = agentCommissionService;
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<AgentCommissionResponse>> getCommissionsByAgent(@PathVariable Long agentId){
        List<AgentCommissionResponse> commissions=agentCommissionService.getAllByAgentId(agentId);

        return ResponseEntity.ok(commissions);
    }


    @GetMapping("/agent/{agentId}/commission-statement")
    public ResponseEntity<CommissionStatement> getCommissionStatement(@PathVariable Long agentId, @RequestParam YearMonth yearMonth){
        CommissionStatement statement=agentCommissionService.getByAgentAndMonth(agentId,yearMonth);

        return ResponseEntity.ok(statement);
    }

    @GetMapping("/recovery-required")
    public ResponseEntity<RecoveryResponse> getAllRequiredRecoveries(){
        RecoveryResponse recoveryResponse=agentCommissionService.getAllRecoveries();

        return ResponseEntity.ok(recoveryResponse);
    }

    @PatchMapping("/{commissionId}/pay")
    public ResponseEntity<String> pay(@PathVariable Long commissionId){
        agentCommissionService.payCommission(commissionId);

        return ResponseEntity.ok("Commission paid");
    }
}
