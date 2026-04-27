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

    @Override
    public GuestResponse editGuest(Long guestId, GuestRequest request) {
        Guest guest=guestRepository.findById(guestId).orElseThrow(()->new GuestNotFoundException("Guest not found..."));
        guest.setGuestName(request.getGuestName());
        guest.setGuestEmail(request.getGuestEmail());
        guest.setPhone(request.getPhone());

        Guest savedGuest=guestRepository.save(guest);

        return GuestMapper.toResponseDto(savedGuest);
    }

    @Override
    public GuestResponse getByEmail(String email) {
        Guest guest=guestRepository.findByGuestEmail(email).orElseThrow(()->new GuestNotFoundException("Invalid Email"));

        return GuestMapper.toResponseDto(guest);
    }
}
