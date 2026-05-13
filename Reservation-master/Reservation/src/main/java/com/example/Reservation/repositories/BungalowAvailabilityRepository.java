package com.example.Reservation.repositories;

import com.example.Reservation.entities.BungalowAvailability;
import com.example.Reservation.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BungalowAvailabilityRepository extends JpaRepository<BungalowAvailability,Long> {

    Optional<BungalowAvailability> findByIdAndBungalowBungalowId(Long id,Long bungalowId);

    List<BungalowAvailability> findAllByBungalowBungalowId(Long bungalowId);

    @Query("""
    SELECT COUNT(a) > 0 FROM BungalowAvailability a
    WHERE a.bungalow.bungalowId = :bungalowId
    AND a.status = 'AVAILABLE'
    AND a.fromDate <= :fromDate
    AND a.toDate >= :toDate
""")
    boolean isAvailableForDates(
            @Param("bungalowId") Long bungalowId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
    SELECT a FROM BungalowAvailability a
    WHERE a.bungalow.bungalowId = :bungalowId
    AND a.status = 'AVAILABLE'
    AND a.fromDate <= :arrivalDate
    AND a.toDate >= :departureDate
""")
    Optional<BungalowAvailability> findCoveringAvailableRecord(
            @Param("bungalowId") Long bungalowId,
            @Param("arrivalDate") LocalDate arrivalDate,
            @Param("departureDate") LocalDate departureDate
    );

    @Query("""
    SELECT a FROM BungalowAvailability a
    WHERE a.bungalow.bungalowId = :bungalowId
    AND a.status = 'BOOKED'
    AND a.fromDate = :arrivalDate
    AND a.toDate = :departureDate
""")
    Optional<BungalowAvailability> findBookedRecord(
            @Param("bungalowId") Long bungalowId,
            @Param("arrivalDate") LocalDate arrivalDate,
            @Param("departureDate") LocalDate departureDate
    );

    Optional<BungalowAvailability> findByBungalow_BungalowIdAndStatusAndToDate(
            Long bungalowId,
            AvailabilityStatus status,
            LocalDate toDate
    );

    Optional<BungalowAvailability> findByBungalow_BungalowIdAndStatusAndFromDate(
            Long bungalowId,
            AvailabilityStatus status,
            LocalDate fromDate
    );
}
