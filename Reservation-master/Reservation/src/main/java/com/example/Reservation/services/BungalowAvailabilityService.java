package com.example.Reservation.services;

import com.example.Reservation.dtos.AvailabilityCheckResponseDto;
import com.example.Reservation.dtos.BungalowAvailabilityRequest;
import com.example.Reservation.dtos.BungalowAvailabilityResponse;
import com.example.Reservation.enums.AvailabilityStatus;

import java.time.LocalDate;
import java.util.List;

public interface BungalowAvailabilityService {

        BungalowAvailabilityResponse addAvailability(Long bungalowId, BungalowAvailabilityRequest request);

        List<BungalowAvailabilityResponse> getAllAvailabilityByBungalow(Long bungalowId);

        AvailabilityCheckResponseDto checkAvailability(Long bungalowId, LocalDate fromDate,LocalDate toDate);

        BungalowAvailabilityResponse updateAvailability(AvailabilityStatus availabilityStatus,Long bungalowId,Long availabilityId);

        void deleteAvailability(Long bungalowId,Long availabilityId);

}
