package com.example.Reservation.events;

import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.entities.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationConfirmedEvent {

    private ReservationResponse reservation;


}
