package com.example.Reservation.exceptions;

public class CancellationNotFoundException extends RuntimeException {
    public CancellationNotFoundException(String message) {
        super(message);
    }
}
