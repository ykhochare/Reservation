package com.example.Reservation.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestResponse {

    private String guestName;

    private String guestEmail;

    private String phone;

    private Integer loyaltyPoints;
}
