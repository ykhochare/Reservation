package com.example.Reservation.repositories;

import com.example.Reservation.entities.LoyaltyPointsHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoyaltyPointsHistoryRepository extends JpaRepository<LoyaltyPointsHistory,Long> {

    List<LoyaltyPointsHistory> findByGuestGuestId(Long guestId);
}
