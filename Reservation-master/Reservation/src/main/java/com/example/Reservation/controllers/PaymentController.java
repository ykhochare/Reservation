package com.example.Reservation.controllers;

import com.example.Reservation.dtos.PaymentResponse;
import com.example.Reservation.dtos.PaymentSuccessResponse;
import com.example.Reservation.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payments",description = "APIs for managing reservation payments")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Make a payment for reservation",description = "Processes payment for a reservation.Guest can optionally redeem loyalty points as discount")
    @PostMapping("/pay/{reservationId}")
    public ResponseEntity<PaymentSuccessResponse> payAmount(@PathVariable Long reservationId, @RequestParam Double amount,@RequestParam(defaultValue = "0") Integer pointsToUse){

        PaymentSuccessResponse paymentResponse=paymentService.pay(reservationId, amount,pointsToUse);

        return ResponseEntity.ok(paymentResponse);
    }

    @Operation(summary = "Get all payments for reservation",description = "Returns all payment records for a specific reservation")
    @GetMapping("/{reservationId}")
    public ResponseEntity<List<PaymentResponse>> getPayments(@PathVariable Long reservationId){
        List<PaymentResponse> payments=paymentService.getAllPayments(reservationId);

        return ResponseEntity.ok(payments);
    }

}
