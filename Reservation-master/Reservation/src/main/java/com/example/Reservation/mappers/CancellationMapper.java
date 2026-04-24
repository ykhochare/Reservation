package com.example.Reservation.mappers;

import com.example.Reservation.dtos.CancellationResponseDto;
import com.example.Reservation.entities.Cancellation;

public class CancellationMapper {

    public static CancellationResponseDto toResponseDto(Cancellation cancellation){

        CancellationResponseDto dto=new CancellationResponseDto();

        dto.setReservationId(cancellation.getReservation().getId());
        dto.setGuestName(cancellation.getReservation().getGuestName());
        dto.setBungalowId(cancellation.getReservation().getBungalowId());
        dto.setReason(cancellation.getReason());
        dto.setDaysBeforeCheckIn(cancellation.getDaysBeforeCheckIn());
        dto.setTotalAmount(cancellation.getReservation().getTotalAmount());
        dto.setRefundAmount(cancellation.getRefundAmount());
        dto.setRefundStatus(cancellation.getRefundStatus());

        return dto;

    }
}
