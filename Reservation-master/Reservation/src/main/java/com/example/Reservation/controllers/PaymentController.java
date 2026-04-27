package com.example.Reservation.controllers;

import com.example.Reservation.dtos.PaymentResponse;
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
    public ResponseEntity<String> payAmount(@PathVariable Long reservationId, @RequestParam Double amount){
        paymentService.pay(reservationId, amount);

        return ResponseEntity.ok("Payment successful of amount: "+amount);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<List<PaymentResponse>> getPayments(@PathVariable Long reservationId){
        List<PaymentResponse> payments=paymentService.getAllPayments(reservationId);

        return ResponseEntity.ok(payments);
    }

}
