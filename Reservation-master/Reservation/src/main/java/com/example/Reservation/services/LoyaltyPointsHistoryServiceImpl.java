package com.example.Reservation.services;

import com.example.Reservation.dtos.LoyaltyPointsHistoryResponse;
import com.example.Reservation.entities.LoyaltyPointsHistory;
import com.example.Reservation.mappers.LoyaltyPointsHistoryMapper;
import com.example.Reservation.repositories.LoyaltyPointsHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoyaltyPointsHistoryServiceImpl implements LoyaltyPointsHistoryService{

    private final LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository;

    public LoyaltyPointsHistoryServiceImpl(LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository) {
        this.loyaltyPointsHistoryRepository = loyaltyPointsHistoryRepository;
    }

    @Override
    public List<LoyaltyPointsHistoryResponse> getPointsHistoryByGuest(Long guestId) {

        List<LoyaltyPointsHistory> histories=loyaltyPointsHistoryRepository.findByGuestGuestId(guestId);

        return histories.stream().map(LoyaltyPointsHistoryMapper::toResponseDto).toList();
    }
}
