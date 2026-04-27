package com.example.Reservation.repositories;

import com.example.Reservation.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest,Long> {

    Optional<Guest> findByGuestEmail(String guestEmail);
}
