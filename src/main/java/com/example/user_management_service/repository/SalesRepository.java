package com.example.user_management_service.repository;


import com.example.user_management_service.model.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query("SELECT s FROM Sales s WHERE s.salesDate BETWEEN :startDate AND :endDate")
    Page<Sales> findAllBySalesDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

}