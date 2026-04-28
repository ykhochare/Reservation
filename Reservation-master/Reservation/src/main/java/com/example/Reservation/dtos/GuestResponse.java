package com.example.Reservation.dtos;

import com.example.Reservation.enums.LoyaltyTier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestResponse {

    private Long guestId;

    private String guestName;

    private String guestEmail;

    private String phone;

    private Integer loyaltyPoints;

    private LoyaltyTier loyaltyTier;
}
