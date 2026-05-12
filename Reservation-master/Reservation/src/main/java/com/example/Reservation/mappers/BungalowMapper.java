package com.example.Reservation.mappers;

import com.example.Reservation.dtos.BungalowRequest;
import com.example.Reservation.dtos.BungalowResponse;
import com.example.Reservation.entities.Bungalow;

public class BungalowMapper {

    public static Bungalow toEntity(BungalowRequest request){
        Bungalow bungalow=new Bungalow();
        bungalow.setBungalowName(request.getBungalowName());
        bungalow.setPricePerNight(request.getPricePerNight());

        return bungalow;
    }

    public static BungalowResponse toResponseDto(Bungalow bungalow){
        BungalowResponse response=new BungalowResponse();
        response.setBungalowId(bungalow.getBungalowId());
        response.setBungalowName(bungalow.getBungalowName());
        response.setPricePerNight(bungalow.getPricePerNight());

        return response;
    }
}
