package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequest {

    private Long bungalowId;

    private String guestName;

    private String guestEmail;

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    private Double totalAmount;
}
