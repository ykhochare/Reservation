package com.example.Reservation.services;

import com.example.Reservation.dtos.GuestRequest;
import com.example.Reservation.dtos.GuestResponse;

public interface GuestService {

    GuestResponse registerGuest(GuestRequest request);

    GuestResponse getGuestById(Long guestId);

    GuestResponse editGuest(Long guestId,GuestRequest request);

    GuestResponse getByEmail(String email);
}
