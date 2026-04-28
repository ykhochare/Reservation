package com.example.Reservation.mappers;

import com.example.Reservation.dtos.LoyaltyPointsHistoryResponse;
import com.example.Reservation.entities.LoyaltyPointsHistory;

public class LoyaltyPointsHistoryMapper {

    public static LoyaltyPointsHistoryResponse toResponseDto(LoyaltyPointsHistory history){

        LoyaltyPointsHistoryResponse response=new LoyaltyPointsHistoryResponse();

        response.setId(history.getId());
        response.setPoints(history.getPoints());
        response.setPointsType(history.getPointsType());

        return response;
    }
}
