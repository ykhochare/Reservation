package com.example.Reservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponseDto {

    private Long bungalowId;

    private Double totalRevenue;

    private Double totalRefunds;

    private Double netRevenue;
}
