package com.example.Reservation.repositories;

import com.example.Reservation.entities.AgentCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentCommissionRepository extends JpaRepository<AgentCommission,Long> {

    List<AgentCommission> findByTravelAgentAgentId(Long agentId);

    @Query("""
    SELECT a FROM AgentCommission a
    WHERE a.travelAgent.agentId = :agentId
    AND YEAR(a.createdAt) = :year
    AND MONTH(a.createdAt) = :month
    """)
    List<AgentCommission> findByAgentAndMonth(
            @Param("agentId") Long agentId,
            @Param("year") int year,
            @Param("month") int month
    );

    List<AgentCommission> findByRecoveryRequiredTrue();

}
