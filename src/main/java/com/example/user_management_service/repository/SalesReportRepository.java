package com.example.user_management_service.repository;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.sql.Date;

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


//    @Query("""
//    SELECT s FROM SalesReport s
//    WHERE (:medicineId IS NULL OR s.medicine.id = :medicineId)
//    AND (:regionId IS NULL OR s.region.id = :regionId)
//    AND (:contractType IS NULL OR s.contractType = :contractType)
//    AND (:startDate IS NULL OR (s.startDate IS NOT NULL AND s.startDate > :startDate))
//    AND (:endDate IS NULL OR (s.endDate IS NOT NULL AND s.endDate < :endDate))
//    ORDER BY s.id DESC
//    """)
//    List<SalesReport> findByFilters(
//            @Param("medicineId") Long medicineId,
//            @Param("regionId") Long regionId,
//            @Param("contractType") ContractType contractType,
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate
//    );

    @Query("""
                SELECT s FROM SalesReport s
                WHERE (:medicineId IS NULL OR s.medicine.id = :medicineId)
                AND (:regionId IS NULL OR s.region.id = :regionId)
                AND (:contractType IS NULL OR s.contractType = :contractType)
                AND (CAST(:startDate AS date) IS NULL OR s.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR s.endDate <= :endDate)
                ORDER BY s.id DESC
            """)
    List<SalesReport> findByFilters(
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("contractType") ContractType contractType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
                SELECT s 
                FROM SalesReport s 
                WHERE 
                    (:medicineId IS NULL OR s.medicine.id = :medicineId) 
                    AND (
                        (:regionId IS NOT NULL AND s.region.id = :regionId) 
                        OR 
                        (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)
                    )
                AND (CAST(:startDate AS date) IS NULL OR s.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR s.endDate <= :endDate)
                ORDER BY s.id DESC
            """)
    List<SalesReport> findByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'RECIPE' THEN 1 ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND sr.reportDate BETWEEN :startDate AND :endDate")
    Long countRecipeByMedicine(@Param("medicineId") Long medicineId,
                               @Param("regionId") Long regionId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'SU' THEN 1 ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND sr.reportDate BETWEEN :startDate AND :endDate")
    Long countSUByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'SB' THEN 1 ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND sr.reportDate BETWEEN :startDate AND :endDate")
    Long countSBByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'GZ' THEN 1 ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND sr.reportDate BETWEEN :startDate AND :endDate")
    Long countGZByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'KZ' THEN 1 ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND sr.reportDate BETWEEN :startDate AND :endDate")
    Long countKZByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);


    @Query("SELECT COALESCE(SUM(s.written), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'RECIPE' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countRecipeByFilters(@Param("medicineId") Long medicineId,
                              @Param("regionId") Long regionId,
                              @Param("regionIds") List<Long> regionIds,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.written), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'SU' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countSUByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.written), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'SB' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countSBByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.written), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'GZ' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countGZByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.written), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'KZ' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countKZByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
}










