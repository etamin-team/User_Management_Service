package com.example.user_management_service.repository;

import com.example.user_management_service.model.AgentGoal;
import com.example.user_management_service.model.ManagerGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AgentGoalRepository extends JpaRepository<AgentGoal, Long> {
    @Query("SELECT ac FROM AgentGoal ac WHERE ac.medAgent.userId = :medAgentId "+
            "AND ac.startDate <= CURRENT_DATE " +
            "AND ac.endDate > CURRENT_DATE")
    Optional<AgentGoal> getGoalsByMedAgentUserId(@Param("medAgentId") UUID medAgentId);


    @Query("SELECT m FROM AgentGoal m WHERE m.medAgent.userId = :medAgentId " +
            "AND m.endDate IS NULL")
    Optional<AgentGoal> getNullEndDateGoalByAgentId(@Param("medAgentId") UUID medAgentId);
}
