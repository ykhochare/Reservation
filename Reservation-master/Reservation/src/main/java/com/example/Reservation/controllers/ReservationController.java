package com.example.Reservation.controllers;

import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.dtos.RevenueResponseDto;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reservations",description = "APIs for managing hotel reservations")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Create a new reservation",description = "Creates a reservation for a bungalow.Status will be WAITING if bungalow is already booked for given dates")
    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(@RequestBody ReservationRequest request){

        ReservationResponse reservation=reservationService.addReservation(request);

        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @Operation(summary = "Confirm a reservation",description = "Confirms a PENDING reservation and awards loyalty points to guest")
    @PatchMapping("/confirm/{reservationId}")
    public ResponseEntity<ReservationResponse> confirm(@PathVariable Long reservationId){

        ReservationResponse response=reservationService.confirmBooking(reservationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get reservation by Id",description = "Returns a single reservation by its Id")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long reservationId){

        ReservationResponse response=reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all reservations",description = "Returns all reservations with optional filters by status,arrival date,departure date and bungalow Id")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(@RequestParam(required = false) ReservationStatus status,
                                                     @RequestParam(required = false) LocalDate arrivalDate,
                                                     @RequestParam(required = false) LocalDate departureDate,
                                                     @RequestParam(required = false) Long bungalowId){

        List<ReservationResponse> reservationResponses=reservationService.getAllReservations(status, arrivalDate, departureDate, bungalowId);
        return ResponseEntity.ok(reservationResponses);
    }

    @Operation(summary = "Get revenue by bungalow",description = "Returns total revenue and refunded amount for a specific bungalow")
    @GetMapping("/bungalow/{bungalowId}/revenue")
    public ResponseEntity<RevenueResponseDto> getBungalowRevenue(@PathVariable Long bungalowId){
        RevenueResponseDto revenueResponse=reservationService.getRevenueByBungalow(bungalowId);

        return ResponseEntity.ok(revenueResponse);
    }

    @Operation(summary = "Confirms all reservations by agent",description = "Bulk confirms all PENDING reservations linked to a specific travel agent and creates commission for each")
    @PatchMapping("/confirm-by-agent/{agentId}")
    public ResponseEntity<String> confirmByAgent(@PathVariable Long agentId){
        int count=reservationService.confirmAgentReservations(agentId);

        return ResponseEntity.ok(count+" reservations are confirmed.");
    }
}
