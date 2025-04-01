package com.example.user_management_service.repository;

import com.example.user_management_service.model.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Date-2/25/2025
 * By Sardor Tokhirov
 * Time-6:21 AM (GMT+5)
 */
@Repository
public interface SalesReportRepository extends JpaRepository<SalesReport, Long> {
    @Query("SELECT s FROM SalesReport s WHERE :medicineId IS NULL OR s.medicine.id = :medicineId")
    Optional<SalesReport> findByMedicineId(@Param("medicineId") Long medicineId);


    @Query("SELECT s FROM SalesReport s " +
            "WHERE (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND (:regionId IS NULL OR s.region.id = :regionId) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate) " +
            "ORDER BY s.id DESC LIMIT 1")
    Optional<SalesReport> findByFilters(@Param("medicineId") Long medicineId,
                                        @Param("regionId") Long regionId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM SalesReport s " +
            "WHERE (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "    OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate) " +
            "ORDER BY s.id DESC LIMIT 1")
    Optional<SalesReport> findByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


}
