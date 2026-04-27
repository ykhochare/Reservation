package com.example.Reservation.mappers;

import com.example.Reservation.dtos.ReservationRequest;
import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.entities.Reservation;

public class ReservationMapper {

    public static Reservation toEntity(ReservationRequest request){
        Reservation reservation=new Reservation();
        reservation.setBungalowId(request.getBungalowId());
        reservation.setArrivalDate(request.getArrivalDate());
        reservation.setDepartureDate(request.getDepartureDate());
        reservation.setTotalAmount(request.getTotalAmount());

        return reservation;
    }

    public static ReservationResponse toResponseDto(Reservation reservation){
        ReservationResponse response=new ReservationResponse();

        response.setId(reservation.getId());
        response.setBungalowId(reservation.getBungalowId());
        response.setGuestName((reservation.getGuest().getGuestName()));
        response.setArrivalDate(reservation.getArrivalDate());
        response.setDepartureDate(reservation.getDepartureDate());
        response.setTotalAmount(reservation.getTotalAmount());
        response.setStatus(reservation.getStatus());
        response.setGuestEmail(reservation.getGuest().getGuestEmail());
        return response;
    }
}
