package com.example.Reservation.repositories;

import com.example.Reservation.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> getByReservation_Id(Long reservationId);
}
