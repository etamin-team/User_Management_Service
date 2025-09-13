package com.example.user_management_service.repository;


import com.example.user_management_service.model.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query("""
            SELECT s FROM Sales s 
            WHERE (:yearMonth IS NULL OR s.yearMonth = :yearMonth)
            ORDER BY s.medicine.name ASC
            """)
    List<Sales> findAllByYearMonth(
            @Param("yearMonth") YearMonth yearMonth
    );

    @Query("""
            SELECT s FROM Sales s 
            WHERE (:startDate IS NULL OR s.yearMonth >= FUNCTION('YEAR_MONTH', :startDate))
            AND (:endDate IS NULL OR s.yearMonth <= FUNCTION('YEAR_MONTH', :endDate))
            ORDER BY s.id ASC
            """)
    List<Sales> findAllByStartAndEndDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COALESCE(SUM(s.quote), 0) FROM Sales s 
             WHERE (:regionId IS NULL OR s.region.id = :regionId)
             AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)
            """)
    Long getTotalQuotes(
            @Param("regionId") Long regionId,
            @Param("yearMonth") YearMonth yearMonth
    );
    
    @Query("""
            SELECT COALESCE(SUM(s.quote), 0) FROM Sales s 
             WHERE (:regionId IS NULL OR s.region.id = :regionId)
             AND (:startDate IS NULL OR s.yearMonth >= FUNCTION('YEAR_MONTH', :startDate))
             AND (:endDate IS NULL OR s.yearMonth <= FUNCTION('YEAR_MONTH', :endDate))
            """)
    Long getTotalQuotes(
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COALESCE(SUM(s.total), 0) FROM Sales s 
             WHERE (:regionId IS NULL OR s.region.id = :regionId)
             AND (:yearMonth IS NULL OR s.yearMonth = :yearMonth)
            """)
    Long getTotalAmounts(
            @Param("regionId") Long regionId,
            @Param("yearMonth") YearMonth yearMonth
    );
    
    @Query("""
            SELECT COALESCE(SUM(s.total), 0) FROM Sales s 
             WHERE (:regionId IS NULL OR s.region.id = :regionId)
             AND (:startDate IS NULL OR s.yearMonth >= FUNCTION('YEAR_MONTH', :startDate))
             AND (:endDate IS NULL OR s.yearMonth <= FUNCTION('YEAR_MONTH', :endDate))
            """)
    Long getTotalAmounts(
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}