package com.example.Reservation.dtos;

import com.example.Reservation.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancellationResponseDto {

    private Long reservationId;

    private String guestName;

    private Long bungalowId;

    private String reason;

    private Integer daysBeforeCheckIn;

    private Double totalAmount;

    private Double refundAmount;

    private RefundStatus refundStatus;

}
