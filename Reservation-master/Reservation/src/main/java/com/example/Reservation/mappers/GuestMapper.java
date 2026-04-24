package com.example.Reservation.mappers;


import com.example.Reservation.dtos.GuestRequest;
import com.example.Reservation.dtos.GuestResponse;
import com.example.Reservation.entities.Guest;

public class GuestMapper {

    public static Guest toEntity(GuestRequest request){
        Guest guest=new Guest();

        guest.setGuestName(request.getGuestName());
        guest.setGuestEmail(request.getGuestEmail());
        guest.setPhone(request.getPhone());
        guest.setLoyaltyPoints(50);

        return guest;
    }

    public static GuestResponse toResponseDto(Guest guest){

        GuestResponse response=new GuestResponse();

        response.setGuestName(guest.getGuestName());
        response.setGuestEmail(guest.getGuestEmail());
        response.setPhone(guest.getPhone());
        response.setLoyaltyPoints(guest.getLoyaltyPoints());

        return response;
    }
}
