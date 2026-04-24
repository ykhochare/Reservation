package com.example.Reservation.repositories;

import com.example.Reservation.entities.Cancellation;
import com.example.Reservation.enums.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CancellationRepository extends JpaRepository<Cancellation,Long>, JpaSpecificationExecutor<Cancellation> {

    Page<Cancellation> findByRefundStatus(RefundStatus status, Pageable pageable);

    Optional<Cancellation> findByReservation_Id(Long reservationId);
}
