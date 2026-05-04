package com.example.Reservation.repositories;

import com.example.Reservation.entities.AgentCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentCommissionRepository extends JpaRepository<AgentCommission,Long> {

    List<AgentCommission> findByTravelAgentAgentId(Long agentId);

    @Query(value = "select * from agent_commission where agent_id = :agentId and year(created_at) = :year and month(created_at) = :month",nativeQuery = true)
    List<AgentCommission> findByAgentAndMonth(
            @Param("agentId") Long agentId,
            @Param("year") int year,
            @Param("month") int month
    );

    List<AgentCommission> findByRecoveryRequiredTrue();

}
