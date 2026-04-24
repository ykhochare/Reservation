package com.example.Reservation.exceptions;

public class CancellationPolicyNotFoundException extends RuntimeException {
    public CancellationPolicyNotFoundException(String message) {
        super(message);
    }
}
