package com.example.user_management_service.repository;

import com.example.user_management_service.model.MedicineWithQuantityDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MedicineWithQuantityDoctorRepository  extends JpaRepository<MedicineWithQuantityDoctor, Long> {
    @Query("SELECT COALESCE(SUM(m.quote), 0) FROM MedicineWithQuantityDoctor m")
    Long getTotalQuotes();

    @Query(value = "SELECT COALESCE(SUM(m.quote), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "JOIN districts d ON u.district_id = d.id " +
            "JOIN regions r ON d.region_id = r.id " +
            "WHERE r.id = :regionId",
            nativeQuery = true)
    Long getTotalQuotesByRegion(@Param("regionId") Long regionId);
}
