package com.example.Reservation.services;

import com.example.Reservation.dtos.BungalowRequest;
import com.example.Reservation.dtos.BungalowResponse;
import com.example.Reservation.dtos.RevenueResponseDto;

import java.util.List;

public interface BungalowService {

        BungalowResponse addBungalow(BungalowRequest bungalowRequest);

        BungalowResponse updateBungalow(Long bungalowId,BungalowRequest request);

        BungalowResponse getBungalow(Long bungalowId);

        List<BungalowResponse> getAllBungalow();

        RevenueResponseDto getRevenueByBungalow(Long bungalowId);
}
