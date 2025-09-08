package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.MedAgentGoalV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-8/23/2025
 * By Sardor Tokhirov
 * Time-5:45 AM (GMT+5)
 */
@Repository
public interface MedAgentGoalV2Repository extends JpaRepository<MedAgentGoalV2, Long> {
    @Query("SELECT g FROM MedAgentGoalV2 g WHERE g.medAgent.userId = :agentId AND g.startDate <= :currentDate AND g.endDate >= :currentDate")
    Optional<MedAgentGoalV2> findActiveByAgentId(UUID agentId, LocalDate currentDate);


    @Query("SELECT g FROM MedAgentGoalV2 g WHERE g.medAgent.userId IN :agentIds")
    List<MedAgentGoalV2> findByAgentIds(List<UUID> agentIds);

    @Query("SELECT g FROM MedAgentGoalV2 g WHERE g.medAgent.district.region.id = :regionId")
    List<MedAgentGoalV2> findByRegion(Long regionId);
}
