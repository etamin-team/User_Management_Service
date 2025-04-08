package com.example.user_management_service.repository;

import com.example.user_management_service.model.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
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


    @Query(nativeQuery = true, value = """
    SELECT *
    FROM sales_report sr
    WHERE 
        (:medicineId IS NULL OR sr.medicine_id = :medicineId)
        AND (:regionId IS NULL OR sr.region_id = :regionId)
        AND (:startDate IS NULL OR sr.start_date >= :startDate)
        AND (:endDate IS NULL OR sr.end_date <= :endDate)
    ORDER BY sr.id DESC
    LIMIT 1
""")
    Optional<SalesReport> findByFilters(@Param("medicineId") Long medicineId,
                                        @Param("regionId") Long regionId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);




    @Query(nativeQuery = true, value = """
    SELECT *
    FROM sales_report sr
    WHERE 
        (:medicineId IS NULL OR sr.medicine_id = :medicineId)
        AND (
            (:regionId IS NOT NULL AND sr.region_id = :regionId)
            OR (:regionId IS NULL AND :regionIds IS NOT NULL AND sr.region_id IN :regionIds)
        )
        AND (:startDate IS NULL OR sr.start_date >= :startDate)
        AND (:endDate IS NULL OR sr.end_date <= :endDate)
    ORDER BY sr.id DESC
    LIMIT 1
""")
    Optional<SalesReport> findByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);



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










