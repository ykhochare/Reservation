package com.example.Reservation.controllers;

import com.example.Reservation.dtos.TravelAgentRequest;
import com.example.Reservation.dtos.TravelAgentResponse;
import com.example.Reservation.services.TravelAgentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class TravelAgentController {

    private final TravelAgentService travelAgentService;

    public TravelAgentController(TravelAgentService travelAgentService) {
        this.travelAgentService = travelAgentService;
    }

    @PostMapping
    public ResponseEntity<TravelAgentResponse> registerAgent(@RequestBody TravelAgentRequest request){
        TravelAgentResponse response=travelAgentService.addAgent(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<TravelAgentResponse> getAgent(@PathVariable Long agentId){
        TravelAgentResponse response=travelAgentService.getAgentById(agentId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{agentId}")
    public ResponseEntity<TravelAgentResponse> updateAgent(@PathVariable Long agentId,@RequestBody TravelAgentRequest request){
        TravelAgentResponse response= travelAgentService.updateAgent(agentId,request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TravelAgentResponse>> getAgents(){
        List<TravelAgentResponse> agents=travelAgentService.getAllAgents();

        return ResponseEntity.ok(agents);
    }
}
