package com.example.Reservation.controllers;

import com.example.Reservation.dtos.AgentCommissionResponse;
import com.example.Reservation.dtos.CommissionStatement;
import com.example.Reservation.dtos.RecoveryResponse;
import com.example.Reservation.services.AgentCommissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@Tag(name = "Agent Commission",description = "APIs for managing travel agent commissions")
@RestController
@RequestMapping("/api/commissions")
public class AgentCommissionController {

    private final AgentCommissionService agentCommissionService;

    public AgentCommissionController(AgentCommissionService agentCommissionService) {
        this.agentCommissionService = agentCommissionService;
    }

    @Operation(summary = "Get all commissions bt agent",description = "Returns all commission records for specific travel agent")
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<AgentCommissionResponse>> getCommissionsByAgent(@PathVariable Long agentId){
        List<AgentCommissionResponse> commissions=agentCommissionService.getAllByAgentId(agentId);

        return ResponseEntity.ok(commissions);
    }

    @Operation(summary = "Get monthly commission statement",description = "Returns commission summary for specific agent and month")
    @GetMapping("/agent/{agentId}/commission-statement")
    public ResponseEntity<CommissionStatement> getCommissionStatement(@PathVariable Long agentId, @RequestParam YearMonth yearMonth){
        CommissionStatement statement=agentCommissionService.getByAgentAndMonth(agentId,yearMonth);

        return ResponseEntity.ok(statement);
    }

    @Operation(summary = "Get all recovery required commissions",description = "Returns all commissions that were paid but need to be recovered due to cancellation")
    @GetMapping("/recovery-required")
    public ResponseEntity<RecoveryResponse> getAllRequiredRecoveries(){
        RecoveryResponse recoveryResponse=agentCommissionService.getAllRecoveries();

        return ResponseEntity.ok(recoveryResponse);
    }

    @Operation(summary = "Mark commission as paid",description = "Marks a PENDING commissions as PAID after finance team processes payment to agent")
    @PatchMapping("/{commissionId}/pay")
    public ResponseEntity<String> pay(@PathVariable Long commissionId){
        agentCommissionService.payCommission(commissionId);

        return ResponseEntity.ok("Commission paid");
    }
}
