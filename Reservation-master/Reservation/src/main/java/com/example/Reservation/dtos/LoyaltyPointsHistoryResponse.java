package com.example.Reservation.dtos;

import com.example.Reservation.enums.PointsType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoyaltyPointsHistoryResponse {

    private Long id;

    private Integer points;

    private PointsType pointsType;
}
