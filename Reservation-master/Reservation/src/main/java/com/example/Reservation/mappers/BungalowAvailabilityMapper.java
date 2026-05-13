package com.example.Reservation.mappers;

import com.example.Reservation.dtos.BungalowAvailabilityRequest;
import com.example.Reservation.dtos.BungalowAvailabilityResponse;
import com.example.Reservation.entities.BungalowAvailability;

public class BungalowAvailabilityMapper {

    public static BungalowAvailability toEntity(BungalowAvailabilityRequest request){
        BungalowAvailability bungalowAvailability=new BungalowAvailability();

        bungalowAvailability.setFromDate(request.getFromDate());
        bungalowAvailability.setToDate(request.getToDate());
        bungalowAvailability.setStatus(request.getAvailabilityStatus());

        return bungalowAvailability;
    }

    public static BungalowAvailabilityResponse toResponse(BungalowAvailability availability){
        BungalowAvailabilityResponse response=new BungalowAvailabilityResponse();

        response.setId(availability.getId());
        response.setFromDate(availability.getFromDate());
        response.setToDate(availability.getToDate());
        response.setAvailabilityStatus(availability.getStatus());

        return response;
    }
}
