package com.example.user_management_service.repository;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.MedicineWithQuantityDoctor;
import com.example.user_management_service.model.SalesReport;
import com.example.user_management_service.model.dto.TopProductsOnSellDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface MedicineWithQuantityDoctorRepository extends JpaRepository<MedicineWithQuantityDoctor, Long> {


    @Query("""
                SELECT new com.example.user_management_service.model.dto.TopProductsOnSellDTO(m.medicine, SUM(cma.amount)) 
                FROM MedicineWithQuantityDoctor m 
                JOIN m.contractMedicineDoctorAmount cma
                JOIN m.doctorContract dc
                JOIN dc.doctor d
                JOIN d.district dist
                JOIN dist.region reg
                JOIN d.workplace wp
                WHERE (:districtId IS NULL OR dist.id = :districtId)
                AND (:regionId IS NULL OR reg.id = :regionId)
                AND (:workplaceId IS NULL OR wp.id = :workplaceId)
                GROUP BY m.medicine 
                ORDER BY SUM(cma.amount) DESC
                LIMIT 6
            """)
    List<TopProductsOnSellDTO> findTop6MostSoldMedicinesWithFilters(
            @Param("districtId") Long districtId,
            @Param("regionId") Long regionId,
            @Param("workplaceId") Long workplaceId
    );

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

    @Query(value = "SELECT COALESCE(SUM(m.quote), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "WHERE  u.district_id = :districtId",
            nativeQuery = true)
    Long getTotalQuotesByDistrict(
            @Param("districtId") Long districtId);

    @Query("SELECT s FROM MedicineWithQuantityDoctor s WHERE :medicineId IS NULL OR s.medicine.id = :medicineId")
    Optional<MedicineWithQuantityDoctor> findByMedicineId(@Param("medicineId") Long medicineId);

    @Query("""
                SELECT COALESCE(SUM(m.correction), 0) 
                FROM MedicineWithQuantityDoctor m 
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalWrittenInFact(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
                SELECT COALESCE(SUM(m.quote), 0) 
                FROM MedicineWithQuantityDoctor m 
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalAllowed(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
                SELECT COALESCE(SUM(m.contractMedicineDoctorAmount.amount), 0) 
                FROM MedicineWithQuantityDoctor m 
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalWritten(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
                SELECT COALESCE(SUM(m.contractMedicineDoctorAmount.amount), 0)
                FROM MedicineWithQuantityDoctor m
                WHERE 
                    (:medicineId IS NULL OR m.medicine.id = :medicineId)
                    AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType)
                    AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                    AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                    AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
                    AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
                    AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                    AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalWritten(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
    SELECT COALESCE(SUM(m.quote), 0)
    FROM MedicineWithQuantityDoctor m
    WHERE 
        (:medicineId IS NULL OR m.medicine.id = :medicineId)
        AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType)
        AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
        AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
        AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
        AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
        AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
        AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            
""")
    Long findTotalAllowed(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
                SELECT COALESCE(SUM(m.correction), 0)
                FROM MedicineWithQuantityDoctor m
                WHERE 
                    (:medicineId IS NULL OR m.medicine.id = :medicineId)
                    AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType)
                    AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                    AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                    AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
                    AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
                    AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                    AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalWrittenInFact(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


}
