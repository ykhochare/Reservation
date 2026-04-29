package com.example.Reservation.services;

import com.example.Reservation.dtos.TravelAgentRequest;
import com.example.Reservation.dtos.TravelAgentResponse;

import java.util.List;

public interface TravelAgentService {

        TravelAgentResponse addAgent(TravelAgentRequest agentRequest);

        TravelAgentResponse getAgentById(Long agentId);

        TravelAgentResponse updateAgent(Long agentId,TravelAgentRequest agentRequest);

        List<TravelAgentResponse> getAllAgents();
}
