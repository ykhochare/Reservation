package com.example.Reservation.services;

import com.example.Reservation.dtos.GuestRequest;
import com.example.Reservation.dtos.GuestResponse;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.LoyaltyPointsHistory;
import com.example.Reservation.enums.PointsType;
import com.example.Reservation.exceptions.GuestNotFoundException;
import com.example.Reservation.mappers.GuestMapper;
import com.example.Reservation.repositories.GuestRepository;
import com.example.Reservation.repositories.LoyaltyPointsHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class GuestServiceImpl implements GuestService{

    private final GuestRepository guestRepository;

    private final LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository;

    public GuestServiceImpl(GuestRepository guestRepository, LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository) {
        this.guestRepository = guestRepository;
        this.loyaltyPointsHistoryRepository = loyaltyPointsHistoryRepository;
    }

    @Override
    public GuestResponse registerGuest(GuestRequest request) {

        Guest guest= GuestMapper.toEntity(request);

        Guest savedGuest=guestRepository.save(guest);

        saveLoyaltyPointsHistory(savedGuest,50,PointsType.EARNED);
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

    private void saveLoyaltyPointsHistory(Guest guest, Integer points, PointsType pointsType){

        LoyaltyPointsHistory history=new LoyaltyPointsHistory();
        history.setGuest(guest);
        history.setPoints(points);
        history.setPointsType(pointsType);
        loyaltyPointsHistoryRepository.save(history);
    }
}
