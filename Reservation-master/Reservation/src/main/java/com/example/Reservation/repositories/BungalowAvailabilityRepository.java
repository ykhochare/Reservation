package com.example.Reservation.repositories;

import com.example.Reservation.entities.BungalowAvailability;
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
}
