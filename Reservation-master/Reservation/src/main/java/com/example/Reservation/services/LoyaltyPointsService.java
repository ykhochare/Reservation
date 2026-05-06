package com.example.Reservation.services;

import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.LoyaltyPointsHistory;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.LoyaltyTier;
import com.example.Reservation.enums.PointsType;
import com.example.Reservation.repositories.LoyaltyPointsHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class LoyaltyPointsService {

    private final LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository;

    public LoyaltyPointsService(LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository) {
        this.loyaltyPointsHistoryRepository = loyaltyPointsHistoryRepository;
    }

    public int calculateCancellationLoyaltyPoints(Reservation reservation){

        int days= (int) ChronoUnit.DAYS.between(reservation.getArrivalDate(),reservation.getDepartureDate());

        return days*10;

    }

    public int calculateConfirmationLoyaltyPoints(Reservation reservation){

        int days= (int)ChronoUnit.DAYS.between(reservation.getArrivalDate(),reservation.getDepartureDate());

        return days*10;

    }

    public void saveLoyaltyPointsHistory(Guest guest, Integer points, PointsType pointsType){

        LoyaltyPointsHistory history=new LoyaltyPointsHistory();
        history.setGuest(guest);
        history.setPoints(points);
        history.setPointsType(pointsType);
        loyaltyPointsHistoryRepository.save(history);
    }

    public void updateLoyaltyTier(Guest guest) {
        int totalEarned = guest.getTotalPointsEarned();
        if (totalEarned >= 1000) {
            guest.setLoyaltyTier(LoyaltyTier.GOLD);
        } else if (totalEarned >= 500) {
            guest.setLoyaltyTier(LoyaltyTier.SILVER);
        } else {
            guest.setLoyaltyTier(LoyaltyTier.BRONZE);
        }
    }
}
