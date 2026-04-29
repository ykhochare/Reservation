package com.example.Reservation.exceptions;

import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.Reservation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    @ExceptionHandler(GuestNotFoundException.class)
    public ResponseEntity<?> guestNotFound(GuestNotFoundException ex){

        return ResponseEntity.badRequest().body(ex.getMessage()+"Kindly register first.");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleRedemptionConflict(ObjectOptimisticLockingFailureException ex){

        Class<?> entityName=ex.getPersistentClass();

        if(entityName== Guest.class)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Points already redeemed by another request. Please try again.");
        else if(entityName== Reservation.class)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Reservation already modified by another request. Please try again.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict occurred. Please try again.");
    }
}
