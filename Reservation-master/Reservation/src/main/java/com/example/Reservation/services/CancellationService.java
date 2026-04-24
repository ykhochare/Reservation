package com.example.Reservation.services;


import com.example.Reservation.dtos.CancellationResponseDto;
import com.example.Reservation.enums.RefundStatus;

import java.util.List;

public interface CancellationService {

    double cancelReservation(Long reservationId);

    List<CancellationResponseDto> allCancellations(RefundStatus status,Long policyId);

    CancellationResponseDto getById(Long id);

    CancellationResponseDto getByReservation(Long id);
}
