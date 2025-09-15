package com.example.user_management_service.repository;

import com.example.user_management_service.model.FieldForceRegions;
import com.example.user_management_service.model.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-3/25/2025
 * By Sardor Tokhirov
 * Time-2:07 PM (GMT+5)
 */
@Repository
public interface FieldForceRegionsRepository extends JpaRepository<FieldForceRegions, Long> {

    @Query("SELECT f FROM FieldForceRegions f WHERE  f.user.userId = :userId")
    Optional<FieldForceRegions> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM FieldForceRegions f WHERE :regionId IN f.regionIds")
    List<FieldForceRegions> findByRegionId(@Param("regionId") Long regionId);

}