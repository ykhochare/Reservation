package com.example.Reservation.controllers;

import com.example.Reservation.dtos.LoyaltyPointsHistoryResponse;
import com.example.Reservation.services.LoyaltyPointsHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pointsHistory")
public class LoyaltyPointsHistoryController {

    private final LoyaltyPointsHistoryService loyaltyPointsHistoryService;

    public LoyaltyPointsHistoryController(LoyaltyPointsHistoryService loyaltyPointsHistoryService) {
        this.loyaltyPointsHistoryService = loyaltyPointsHistoryService;
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<List<LoyaltyPointsHistoryResponse>> getPointsHistory(@PathVariable Long guestId){
        List<LoyaltyPointsHistoryResponse> historyResponses=loyaltyPointsHistoryService.getPointsHistoryByGuest(guestId);

        return ResponseEntity.ok(historyResponses);
    }
}
