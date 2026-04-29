package com.example.Reservation.services;

import com.example.Reservation.dtos.TravelAgentRequest;
import com.example.Reservation.dtos.TravelAgentResponse;
import com.example.Reservation.entities.TravelAgent;
import com.example.Reservation.mappers.TravelAgentMapper;
import com.example.Reservation.repositories.TravelAgentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelAgentServiceImpl implements TravelAgentService{

    private final TravelAgentRepository travelAgentRepository;

    public TravelAgentServiceImpl(TravelAgentRepository travelAgentRepository) {
        this.travelAgentRepository = travelAgentRepository;
    }

    @Override
    public TravelAgentResponse addAgent(TravelAgentRequest agentRequest) {
        TravelAgent travelAgent= TravelAgentMapper.toEntity(agentRequest);

        TravelAgent savedAgent=travelAgentRepository.save(travelAgent);
        return TravelAgentMapper.toResponseDto(savedAgent);
    }

    @Override
    public TravelAgentResponse getAgentById(Long agentId) {

        TravelAgent travelAgent=travelAgentRepository.findById(agentId).orElseThrow(()->new RuntimeException("Agent not found..."));

        return TravelAgentMapper.toResponseDto(travelAgent);
    }

    @Override
    public TravelAgentResponse updateAgent(Long agentId, TravelAgentRequest agentRequest) {
        TravelAgent travelAgent=travelAgentRepository.findById(agentId).orElseThrow(()->new RuntimeException("Agent not found..."));
        travelAgent.setAgentName(agentRequest.getAgentName());
        travelAgent.setAgentEmail(agentRequest.getAgentEmail());
        travelAgent.setCommissionRate(agentRequest.getCommissionRate());

        TravelAgent savedAgent=travelAgentRepository.save(travelAgent);
        return TravelAgentMapper.toResponseDto(savedAgent);
    }

    @Override
    public List<TravelAgentResponse> getAllAgents() {

        List<TravelAgent> agents=travelAgentRepository.findAll();

        return agents.stream().map(TravelAgentMapper::toResponseDto).toList();
    }
}
