package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BungalowResponse {
    private Long bungalowId;

    private String bungalowName;

    private Double pricePerNight;
}
