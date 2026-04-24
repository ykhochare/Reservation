package com.example.Reservation.dtos;

import com.example.Reservation.enums.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationResponse {
    private Long id;
    private Long bungalowId;
    private String guestName;
    private String guestEmail;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private Double totalAmount;
    private ReservationStatus status;
}
