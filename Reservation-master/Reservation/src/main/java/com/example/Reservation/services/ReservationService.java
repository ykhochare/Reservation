package com.example.Reservation.services;

import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.dtos.RevenueResponseDto;
import com.example.Reservation.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    ReservationResponse addReservation(ReservationRequest request);

    ReservationResponse confirmBooking(Long id);

    List<ReservationResponse> getAllReservations(ReservationStatus status, LocalDate arrivalDate,LocalDate departureDate,Long bungalowId);

    ReservationResponse getReservationById(Long id);

    RevenueResponseDto getRevenueByBungalow(Long bungalowId);

    int confirmAgentReservations(Long agentId);
}
