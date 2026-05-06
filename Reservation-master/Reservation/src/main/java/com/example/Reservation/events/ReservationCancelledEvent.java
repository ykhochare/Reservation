package com.example.Reservation.events;

import com.example.Reservation.entities.Reservation;
import lombok.Getter;

@Getter
public class ReservationCancelledEvent {

    private final Reservation reservation;

    private final double refundAmount;

    public ReservationCancelledEvent(Reservation reservation, double refundAmount) {
        this.reservation = reservation;
        this.refundAmount = refundAmount;
    }


}
