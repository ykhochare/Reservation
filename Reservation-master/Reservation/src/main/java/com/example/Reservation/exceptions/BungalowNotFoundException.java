package com.example.Reservation.exceptions;

public class BungalowNotFoundException extends RuntimeException {
    public BungalowNotFoundException(String message) {
        super(message);
    }
}
