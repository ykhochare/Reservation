package com.example.Reservation.dtos;

import com.example.Reservation.enums.AvailabilityStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BungalowAvailabilityRequest {

    private LocalDate fromDate;

    private LocalDate toDate;

    private AvailabilityStatus availabilityStatus;
}
