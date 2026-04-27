package com.example.Reservation.repositories;

import com.example.Reservation.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> getByReservation_Id(Long reservationId);

    @Query(value = "select sum(amount) from reservations r join payment p on r.id=p.reservation_id where bungalow_id= :bungalowId",nativeQuery = true)
    Double calculateRevenueByBungalow(@Param("bungalowId") Long bungalowId);
}
