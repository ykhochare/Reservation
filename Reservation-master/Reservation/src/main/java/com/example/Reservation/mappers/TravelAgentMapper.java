package com.example.Reservation.mappers;

import com.example.Reservation.dtos.TravelAgentRequest;
import com.example.Reservation.dtos.TravelAgentResponse;
import com.example.Reservation.entities.TravelAgent;

public class TravelAgentMapper {

    public static TravelAgent toEntity(TravelAgentRequest request){
        TravelAgent travelAgent=new TravelAgent();
        travelAgent.setAgentName(request.getAgentName());
        travelAgent.setAgentEmail(request.getAgentEmail());
        travelAgent.setCommissionRate(request.getCommissionRate());

        return travelAgent;
    }

    public static TravelAgentResponse toResponseDto(TravelAgent travelAgent){

        TravelAgentResponse response=new TravelAgentResponse();
        response.setAgentId(travelAgent.getAgentId());
        response.setAgentName(travelAgent.getAgentName());
        response.setAgentEmail(travelAgent.getAgentEmail());
        response.setCommissionRate(travelAgent.getCommissionRate());

        return response;
    }
}
