package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BungalowRequest {
    private String bungalowName;

    private Double pricePerNight;
}
