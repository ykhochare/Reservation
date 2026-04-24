package com.example.Reservation.controllers;

import com.example.Reservation.dtos.CancellationResponse;
import com.example.Reservation.dtos.CancellationResponseDto;
import com.example.Reservation.enums.RefundStatus;
import com.example.Reservation.services.CancellationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancellations")
public class CancellationController {

    private final CancellationService cancellationService;

    public CancellationController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<CancellationResponse> cancel(@PathVariable Long reservationId){
        double refund=cancellationService.cancelReservation(reservationId);
        String message="Your reservation is successfully cancelled";

        return ResponseEntity.ok(new CancellationResponse(message,refund));

    }

    @GetMapping
    public ResponseEntity<List<CancellationResponseDto>> cancellations(@RequestParam(required = false) RefundStatus status,
                                                                       @RequestParam(required = false) Long policyId){

        List<CancellationResponseDto> list=cancellationService.allCancellations(status,policyId);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{cancellationId}")
    public ResponseEntity<CancellationResponseDto> getCancellation(@PathVariable Long cancellationId){
        CancellationResponseDto cancellation=cancellationService.getById(cancellationId);

        return ResponseEntity.ok(cancellation);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<CancellationResponseDto> getCancellationByReservation(@PathVariable Long reservationId){
        CancellationResponseDto cancellation=cancellationService.getByReservation(reservationId);

        return ResponseEntity.ok(cancellation);
    }
}
