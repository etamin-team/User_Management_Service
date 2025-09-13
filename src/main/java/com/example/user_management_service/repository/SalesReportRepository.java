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
import java.time.YearMonth;
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

    @Query("SELECT sr FROM SalesReport sr WHERE sr.medicine.id = :medicineId AND sr.region.id = :regionId AND sr.yearMonth = :yearMonth")
    List<SalesReport> findByMedicineIdAndRegionIdAndYearMonth(@Param("medicineId") Long medicineId, @Param("regionId") Long regionId, @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT sr FROM SalesReport sr WHERE sr.medicine.id = :medicineId AND sr.region.id = :regionId AND sr.yearMonth = :yearMonth order by sr.id limit 1")
    SalesReport findSalesReportByMedicineIdAndRegionIdAndYearMonth(@Param("medicineId") Long medicineId, @Param("regionId") Long regionId, @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT sr FROM SalesReport sr WHERE  sr.region.id = :regionId AND sr.yearMonth = :yearMonth")
    List<SalesReport> findByRegionIdAndYearMonth( @Param("regionId") Long regionId, @Param("yearMonth") YearMonth yearMonth);

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
                AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)
                ORDER BY s.id DESC limit 1
            """)
    Optional<SalesReport> findByFilters(
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("contractType") ContractType contractType,
            @Param("yearMonth") YearMonth yearMonth
    );

    @Query("""
                    SELECT s FROM SalesReport s
                    WHERE (:medicineId IS NULL OR s.medicine.id = :medicineId)
                    AND (:regionId IS NULL OR s.region.id = :regionId)
                    AND (:contractType IS NULL OR s.contractType = :contractType)
                    AND (:startDate IS NULL OR s.reportDate >= :startDate)
                    AND (:endDate IS NULL OR s.reportDate <= :endDate)
                    ORDER BY s.id DESC
                """)
        List<SalesReport> findByDateRange(
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
                AND (:contractType IS NULL OR s.contractType = :contractType)
                AND (:startDate IS NULL OR s.reportDate >= :startDate)
                AND (:endDate IS NULL OR s.reportDate <= :endDate)
                ORDER BY s.id DESC
            """)
    List<SalesReport> findByDateRange(
            @Param("regionIds") List<Long> regionIds,
            @Param("contractType") ContractType contractType,
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
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
                AND (:contractType IS NULL OR s.contractType = :contractType)
                AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)
                ORDER BY s.id DESC
            """)
    List<SalesReport> findByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("contractType") ContractType contractType,
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("yearMonth") YearMonth yearMonth
    );

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'RECIPE' THEN sr.sold ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND (:yearMonth IS NULL OR sr.yearMonth = :yearMonth)")
    Long countRecipeByMedicine(@Param("medicineId") Long medicineId,
                               @Param("regionId") Long regionId,
                               @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'SU' THEN sr.sold ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND (:yearMonth IS NULL OR sr.yearMonth = :yearMonth)")
    Long countSUByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'SB' THEN sr.sold ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND (:yearMonth IS NULL OR sr.yearMonth = :yearMonth)")
    Long countSBByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'GZ' THEN sr.sold ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND (:yearMonth IS NULL OR sr.yearMonth = :yearMonth)")
    Long countGZByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(CASE WHEN sr.contractType = 'KZ' THEN sr.sold ELSE 0 END), 0) " +
            "FROM SalesReport sr " +
            "WHERE sr.medicine.id = :medicineId " +
            "AND (:regionId IS NULL OR sr.region.id = :regionId) " +
            "AND (:yearMonth IS NULL OR sr.yearMonth = :yearMonth)")
    Long countKZByMedicine(@Param("medicineId") Long medicineId,
                           @Param("regionId") Long regionId,
                           @Param("yearMonth") YearMonth yearMonth);


    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'RECIPE' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)")
    Long countRecipeByFilters(@Param("medicineId") Long medicineId,
                              @Param("regionId") Long regionId,
                              @Param("regionIds") List<Long> regionIds,
                              @Param("yearMonth") YearMonth yearMonth);


    @Query("SELECT COALESCE(SUM(s.allowed), 0) FROM SalesReport s " +
            "WHERE  " +
            " (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)")
    Long countAllowedByFilters(@Param("medicineId") Long medicineId,
                              @Param("regionId") Long regionId,
                              @Param("regionIds") List<Long> regionIds,
                              @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(s.allowed), 0) FROM SalesReport s " +
            "WHERE  " +
            " (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND (:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)")
    Long countAllowedByFilters(@Param("medicineId") Long medicineId,
                               @Param("regionId") Long regionId,
                               @Param("yearMonth") YearMonth yearMonth);
                               
    @Query("SELECT COALESCE(SUM(s.allowed), 0) FROM SalesReport s " +
            "WHERE  " +
            " (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND (:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countAllowedByDateRange(@Param("medicineId") Long medicineId,
                               @Param("regionId") Long regionId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
                               
    @Query("SELECT COALESCE(SUM(s.allowed), 0) FROM SalesReport s " +
            "WHERE  " +
            " (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countAllowedByDateRange(@Param("medicineId") Long medicineId,
                               @Param("regionId") Long regionId,
                               @Param("regionIds") List<Long> regionIds,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'SU' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)")
    Long countSUByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'SB' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)")
    Long countSBByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'GZ' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)")
    Long countGZByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'KZ' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)")
    Long countKZByFilters(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("yearMonth") YearMonth yearMonth);
                          
    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'RECIPE' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countRecipeByDateRange(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'SU' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countSUByDateRange(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'SB' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countSBByDateRange(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'GZ' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countGZByDateRange(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(s.sold), 0) FROM SalesReport s " +
            "WHERE s.contractType = 'KZ' " +
            "AND (:medicineId IS NULL OR s.medicine.id = :medicineId) " +
            "AND ((:regionId IS NOT NULL AND s.region.id = :regionId) " +
            "   OR (:regionId IS NULL AND :regionIds IS NOT NULL AND s.region.id IN :regionIds)) " +
            "AND (:startDate IS NULL OR s.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.reportDate <= :endDate)")
    Long countKZByDateRange(@Param("medicineId") Long medicineId,
                          @Param("regionId") Long regionId,
                          @Param("regionIds") List<Long> regionIds,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
}










