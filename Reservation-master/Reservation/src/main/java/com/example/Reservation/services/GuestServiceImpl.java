package com.example.Reservation.services;

import com.example.Reservation.dtos.GuestRequest;
import com.example.Reservation.dtos.GuestResponse;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.exceptions.GuestNotFoundException;
import com.example.Reservation.mappers.GuestMapper;
import com.example.Reservation.repositories.GuestRepository;
import org.springframework.stereotype.Service;

@Service
public class GuestServiceImpl implements GuestService{

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public GuestResponse registerGuest(GuestRequest request) {

        Guest guest= GuestMapper.toEntity(request);

        Guest savedGuest=guestRepository.save(guest);

        return GuestMapper.toResponseDto(savedGuest);
    }

    @Override
    public GuestResponse getGuestById(Long guestId) {

        Guest guest=guestRepository.findById(guestId).orElseThrow(()->new GuestNotFoundException("Guest not found..."));

        return GuestMapper.toResponseDto(guest);
    }
}
