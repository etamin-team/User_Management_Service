package com.example.user_management_service.repository;

import com.example.user_management_service.model.AgentGoal;
import com.example.user_management_service.model.District;
import com.example.user_management_service.model.MedAgentGroup;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-6/9/2025
 * By Sardor Tokhirov
 * Time-7:19 AM (GMT+5)
 */
@Repository
public interface MedAgentGroupRepository extends JpaRepository<MedAgentGroup, Long> {
    @Query("SELECT d FROM MedAgentGroup d WHERE d.user.district.region.id = :regionId AND d.user.status = 'ENABLED'")
    List<MedAgentGroup> findByRegionId(@Param("regionId") Long regionId);
    @Query("SELECT d FROM MedAgentGroup d WHERE d.user.userId = :userId")
    Optional<MedAgentGroup> findByUserId(UUID userId);
}
