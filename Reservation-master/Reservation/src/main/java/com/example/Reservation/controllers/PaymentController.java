package com.example.Reservation.controllers;

import com.example.Reservation.dtos.PaymentResponse;
import com.example.Reservation.dtos.PaymentSuccessResponse;
import com.example.Reservation.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay/{reservationId}")
    public ResponseEntity<PaymentSuccessResponse> payAmount(@PathVariable Long reservationId, @RequestParam Double amount,@RequestParam(defaultValue = "0") Integer pointsToUse){

        PaymentSuccessResponse paymentResponse=paymentService.pay(reservationId, amount,pointsToUse);

        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<List<PaymentResponse>> getPayments(@PathVariable Long reservationId){
        List<PaymentResponse> payments=paymentService.getAllPayments(reservationId);

        return ResponseEntity.ok(payments);
    }

}
