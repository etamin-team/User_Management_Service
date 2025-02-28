package com.example.user_management_service.repository;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.ManagerGoal;
import com.example.user_management_service.model.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
