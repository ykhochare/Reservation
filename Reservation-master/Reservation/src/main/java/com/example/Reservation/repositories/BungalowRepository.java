package com.example.Reservation.repositories;

import com.example.Reservation.entities.Bungalow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BungalowRepository extends JpaRepository<Bungalow,Long> {
}
