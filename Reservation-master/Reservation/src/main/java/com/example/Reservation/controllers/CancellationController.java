package com.example.Reservation.controllers;

import com.example.Reservation.dtos.CancellationResponse;
import com.example.Reservation.dtos.CancellationResponseDto;
import com.example.Reservation.enums.RefundStatus;
import com.example.Reservation.services.CancellationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cancellations",description = "APIs for managing cancellation of reservation and refunds")
@RestController
@RequestMapping("/api/cancellations")
public class CancellationController {

    private final CancellationService cancellationService;

    public CancellationController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @Operation(summary = "Cancel a reservation",description = "Cancels a CONFIRMED reservations and calculates refund based on cancellation policy")
    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<CancellationResponse> cancel(@PathVariable Long reservationId){
        double refund=cancellationService.cancelReservation(reservationId);
        String message="Your reservation is successfully cancelled";

        return ResponseEntity.ok(new CancellationResponse(message,refund));

    }

    @Operation(summary = "Get all cancellations",description = "Returns all cancellations by optional filters by refund status and policy")
    @GetMapping
    public ResponseEntity<List<CancellationResponseDto>> cancellations(@RequestParam(required = false) RefundStatus status,
                                                                       @RequestParam(required = false) Long policyId){

        List<CancellationResponseDto> list=cancellationService.allCancellations(status,policyId);

        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get cancellation by Id",description = "Returns a single cancellation record by its Id")
    @GetMapping("/{cancellationId}")
    public ResponseEntity<CancellationResponseDto> getCancellation(@PathVariable Long cancellationId){
        CancellationResponseDto cancellation=cancellationService.getById(cancellationId);

        return ResponseEntity.ok(cancellation);
    }

    @Operation(summary = "Get cancellation by reservation",description ="Returns cancellation record linked to a specific reservation" )
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<CancellationResponseDto> getCancellationByReservation(@PathVariable Long reservationId){
        CancellationResponseDto cancellation=cancellationService.getByReservation(reservationId);

        return ResponseEntity.ok(cancellation);
    }
}
