package com.example.Reservation.controllers;

import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(@RequestBody ReservationRequest request){

        ReservationResponse reservation=reservationService.addReservation(request);

        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @PatchMapping("/confirm/{reservationId}")
    public ReservationResponse confirm(@PathVariable Long reservationId){

        return reservationService.confirmBooking(reservationId);
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse getReservation(@PathVariable Long reservationId){

        return reservationService.getReservationById(reservationId);
    }

    @GetMapping
    public List<ReservationResponse> getReservations(@RequestParam(required = false) ReservationStatus status,
                                                     @RequestParam(required = false) LocalDate arrivalDate,
                                                     @RequestParam(required = false) LocalDate departureDate,
                                                     @RequestParam(required = false) Long bungalowId){

        return reservationService.getAllReservations(status, arrivalDate, departureDate, bungalowId);
    }

}
