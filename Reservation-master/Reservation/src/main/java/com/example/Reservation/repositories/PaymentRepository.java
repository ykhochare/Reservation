package com.example.Reservation.repositories;

import com.example.Reservation.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> getByReservation_Id(Long reservationId);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p
    WHERE p.reservation.bungalow.bungalowId = :bungalowId
    """)
    Double calculateRevenueByBungalow(@Param("bungalowId") Long bungalowId);
}
