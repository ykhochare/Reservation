package com.example.Reservation.repositories;

import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation,Long>, JpaSpecificationExecutor<Reservation> {

    @Query("""
    SELECT COUNT(r) > 0
    FROM Reservation r
    WHERE r.bungalow.bungalowId = :id
      AND r.status = :status
      AND r.departureDate > :newArrivalDate
      AND r.arrivalDate < :newDepartureDate""")
    boolean existsOverlap(
            Long id,
            ReservationStatus status,
            LocalDate newArrivalDate,
            LocalDate newDepartureDate
    );

    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    @Query("""
    SELECT r FROM Reservation r
    WHERE r.bungalow.bungalowId = :bungalowId
    AND r.status = :status
    ORDER BY r.totalAmount DESC, r.createdAt ASC
""")
    List<Reservation> findTopWaitingReservation(
            @Param("bungalowId") Long bungalowId,
            @Param("status") ReservationStatus status,
            Pageable pageable
    );

    Page<Reservation> findByStatusAndArrivalDateBefore(
            ReservationStatus status,
            LocalDate date,
            Pageable pageable
    );

    List<Reservation> findByTravelAgentAgentIdAndStatus(Long agentId, ReservationStatus status);
}
