package com.example.Reservation.controllers;

import com.example.Reservation.dtos.LoyaltyPointsHistoryResponse;
import com.example.Reservation.services.LoyaltyPointsHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Loyalty Points History",description = "APIs for viewing guest loyalty points history")
@RestController
@RequestMapping("/api/pointsHistory")
public class LoyaltyPointsHistoryController {

    private final LoyaltyPointsHistoryService loyaltyPointsHistoryService;

    public LoyaltyPointsHistoryController(LoyaltyPointsHistoryService loyaltyPointsHistoryService) {
        this.loyaltyPointsHistoryService = loyaltyPointsHistoryService;
    }

    @Operation(summary = "Get loyalty points history by guest",description = "Returns all loyalty points transactions for a specific guest including earned" +
            "redeemed and expired points")
    @GetMapping("/guest/{guestId}")
    public ResponseEntity<List<LoyaltyPointsHistoryResponse>> getPointsHistory(@PathVariable Long guestId){
        List<LoyaltyPointsHistoryResponse> historyResponses=loyaltyPointsHistoryService.getPointsHistoryByGuest(guestId);

        return ResponseEntity.ok(historyResponses);
    }
}
