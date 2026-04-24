package com.example.Reservation.repositories;

import com.example.Reservation.entities.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy,Long> {

    @Query(value = "select * from cancellation_policies where :days between days_before_checkin_from and days_before_checkin_to", nativeQuery = true)
    CancellationPolicy findByDaysBeforeCheckIn(@Param("days") Integer days);

    @Query("SELECT COUNT(c) > 0 FROM CancellationPolicy c WHERE :from <= c.daysBeforeCheckInTo AND :to >= c.daysBeforeCheckInFrom")
    boolean existsOverlappingPolicy(@Param("from") Integer from, @Param("to") Integer to);
}
