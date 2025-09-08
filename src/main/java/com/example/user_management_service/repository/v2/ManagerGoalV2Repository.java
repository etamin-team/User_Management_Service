package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.AgentGoal;
import com.example.user_management_service.model.ManagerGoal;
import com.example.user_management_service.model.v2.ManagerGoalV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-4:44 PM (GMT+5)
 */
@Repository
public interface ManagerGoalV2Repository extends JpaRepository<ManagerGoalV2, Long> {

    @Query("SELECT m FROM ManagerGoalV2 m WHERE m.managerId.userId = :managerId " +
            "AND m.status = 'APPROVED' " +
            "AND (m.endDate IS NULL OR m.endDate >= :currentDate)")
    Optional<ManagerGoalV2> getGoalsByManagerId(@Param("managerId") UUID managerId,
                                                @Param("currentDate") LocalDate currentDate);
}
