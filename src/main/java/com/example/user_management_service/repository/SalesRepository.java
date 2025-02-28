package com.example.user_management_service.repository;


import com.example.user_management_service.model.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query("""
        SELECT s FROM Sales s 
        WHERE (:startDate IS NULL OR s.startDate >= :startDate) AND  (:endDate IS NULL OR s.endDate <= :endDate)
        """)
    List<Sales> findAllByStartAndEndDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}