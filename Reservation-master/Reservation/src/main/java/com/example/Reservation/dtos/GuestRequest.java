package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestRequest {

    private String guestName;

    private String guestEmail;

    private String phone;
}
