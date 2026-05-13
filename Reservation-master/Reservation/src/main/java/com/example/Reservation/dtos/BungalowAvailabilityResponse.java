package com.example.Reservation.dtos;

import com.example.Reservation.enums.AvailabilityStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class BungalowAvailabilityResponse {

    private Long id;

    private LocalDate fromDate;

    private LocalDate toDate;

    private AvailabilityStatus availabilityStatus;
}
