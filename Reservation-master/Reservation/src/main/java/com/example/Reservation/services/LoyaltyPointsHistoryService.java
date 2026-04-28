package com.example.Reservation.services;

import com.example.Reservation.dtos.LoyaltyPointsHistoryResponse;

import java.util.List;

public interface LoyaltyPointsHistoryService {

    List<LoyaltyPointsHistoryResponse> getPointsHistoryByGuest(Long guestId);
}
