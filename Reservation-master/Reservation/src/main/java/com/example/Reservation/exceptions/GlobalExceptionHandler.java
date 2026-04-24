package com.example.Reservation.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<?> reservationNotFound(ReservationNotFoundException ex){

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CancellationPolicyNotFoundException.class)
    public ResponseEntity<?> policyNotFound(CancellationPolicyNotFoundException ex){

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> genericException(Exception ex){

        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
