package com.example.user_management_service.repository;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.MedicineWithQuantityDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


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

    @Query(value = "SELECT COALESCE(SUM(m.quote), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "WHERE c.doctor_id = :userId",
            nativeQuery = true)
    Long getTotalQuotesByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT COALESCE(SUM(COALESCE(m.quote, 0)), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "WHERE u.workplace_id = :workplaceId AND u.field_name = :fieldName",
            nativeQuery = true)
    Long getTotalQuotesByWorkplaceAndField(
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") String fieldName);


    @Query(value = "SELECT COALESCE(SUM(m.quote), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "WHERE   u.workplace_id = :workplaceId",
            nativeQuery = true)
    Long getTotalQuotesByDistrictAndWorkplace(

            @Param("workplaceId") Long workplaceId);

}
