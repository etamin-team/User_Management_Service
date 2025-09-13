package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.ReportSaving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

/**
 * Date-9/13/2025
 * By Sardor Tokhirov
 * Time-7:02 PM (GMT+5)
 */
@Repository
public interface ReportSavingRepository extends JpaRepository<ReportSaving, Long> {
    @Query("SELECT rs FROM ReportSaving rs WHERE rs.region.id = :regionId AND rs.yearMonth = :yearMonth ORDER BY rs.id LIMIT 1")
    ReportSaving findOneByRegionIdAndYearMonth(@Param("regionId") Long regionId, @Param("yearMonth") YearMonth yearMonth);
    @Query("SELECT rs FROM ReportSaving rs WHERE rs.region.id = :regionId ")
    List<ReportSaving> findOneByRegionId(@Param("regionId") Long regionId);
}
