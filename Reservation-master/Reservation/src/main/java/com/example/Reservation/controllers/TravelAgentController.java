package com.example.Reservation.controllers;

import com.example.Reservation.dtos.TravelAgentRequest;
import com.example.Reservation.dtos.TravelAgentResponse;
import com.example.Reservation.services.TravelAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Travel Agents",description = "Registers a new travel agent with commission rate")
@RestController
@RequestMapping("/api/agents")
public class TravelAgentController {

    private final TravelAgentService travelAgentService;

    public TravelAgentController(TravelAgentService travelAgentService) {
        this.travelAgentService = travelAgentService;
    }

    @Operation(summary = "Register a new travel agent",description = "Registers a new travel agent with commission rate")
    @PostMapping
    public ResponseEntity<TravelAgentResponse> registerAgent(@RequestBody TravelAgentRequest request){
        TravelAgentResponse response=travelAgentService.addAgent(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get travel agent by Id",description = "Returns travel agent details by Id")
    @GetMapping("/{agentId}")
    public ResponseEntity<TravelAgentResponse> getAgent(@PathVariable Long agentId){
        TravelAgentResponse response=travelAgentService.getAgentById(agentId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update travel agent",description = "Updates existing travel agent including commission rate")
    @PutMapping("/{agentId}")
    public ResponseEntity<TravelAgentResponse> updateAgent(@PathVariable Long agentId,@RequestBody TravelAgentRequest request){
        TravelAgentResponse response= travelAgentService.updateAgent(agentId,request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all travel agents",description = "Returns list of all registers travel agents")
    @GetMapping
    public ResponseEntity<List<TravelAgentResponse>> getAgents(){
        List<TravelAgentResponse> agents=travelAgentService.getAllAgents();

        return ResponseEntity.ok(agents);
    }
}
