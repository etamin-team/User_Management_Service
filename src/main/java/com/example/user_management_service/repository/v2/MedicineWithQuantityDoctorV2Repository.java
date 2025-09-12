package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.dto.SimpleDoctorPrescriptionDTO;
import com.example.user_management_service.model.dto.TopProductsOnSellDTO;
import com.example.user_management_service.model.v2.MedicineWithQuantityDoctorV2;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-8/23/2025
 * By Sardor Tokhirov
 * Time-5:46 AM (GMT+5)
 */
@Repository
public interface MedicineWithQuantityDoctorV2Repository extends JpaRepository<MedicineWithQuantityDoctorV2, Long> {
    @Query("SELECT m FROM MedicineWithQuantityDoctorV2 m WHERE m.doctorContract.id = :contractId")
    List<MedicineWithQuantityDoctorV2> findByDoctorContractId(@Param("contractId") Long contractId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM medicine_with_quantity_doctor_v2 WHERE contract_id = :contractId", nativeQuery = true)
    void deleteByContractIdNative(@Param("contractId") Long contractId);
    
    @Query("""
                SELECT new com.example.user_management_service.model.dto.TopProductsOnSellDTO(m.medicine, SUM(cma.amount)) 
                FROM MedicineWithQuantityDoctorV2 m 
                JOIN m.contractMedicineDoctorAmountV2s cma
                JOIN m.doctorContract dc
                JOIN dc.doctor d
                JOIN d.district dist
                JOIN dist.region reg
                JOIN d.workplace wp
                WHERE (:districtId IS NULL OR dist.id = :districtId)
                AND (:regionId IS NULL OR reg.id = :regionId)
                AND (:workplaceId IS NULL OR wp.id = :workplaceId)
                AND (CAST(:startDate AS date) IS NULL OR dc.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR dc.endDate <= :endDate)
                GROUP BY m.medicine 
                ORDER BY SUM(cma.amount) DESC
                LIMIT 6
            """)
    List<TopProductsOnSellDTO> findTop6MostSoldMedicinesWithFilters(
            @Param("districtId") Long districtId,
            @Param("regionId") Long regionId,
            @Param("workplaceId") Long workplaceId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT s FROM MedicineWithQuantityDoctorV2 s WHERE (:medicineId IS NULL OR s.medicine.id = :medicineId ) AND  (:contractId IS NULL  OR s.doctorContract.id = :contractId)  ")
    Optional<MedicineWithQuantityDoctorV2> findByMedicineIdAndContractId(@Param("medicineId") Long medicineId, @Param("contractId") Long contractId);
    
    @Query("""
                SELECT COALESCE(SUM(m.quote), 0) 
                FROM MedicineWithQuantityDoctorV2 m 
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
                SELECT COALESCE(SUM(cma.amount), 0) 
                FROM MedicineWithQuantityDoctorV2 m 
                JOIN m.contractMedicineDoctorAmountV2s cma
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
                SELECT COALESCE(SUM(cma.correction), 0) 
                FROM MedicineWithQuantityDoctorV2 m 
                JOIN m.contractMedicineDoctorAmountV2s cma
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
                FROM MedicineWithQuantityDoctorV2 m 
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
                AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
                AND m.doctorContract.status='APPROVED'
                AND (CAST(:yearMonth AS date) IS NULL OR (
                    EXTRACT(YEAR FROM m.doctorContract.startDate) * 100 + EXTRACT(MONTH FROM m.doctorContract.startDate) <= 
                    EXTRACT(YEAR FROM CAST(:yearMonth AS date)) * 100 + EXTRACT(MONTH FROM CAST(:yearMonth AS date)) AND 
                    (m.doctorContract.endDate IS NULL OR 
                    EXTRACT(YEAR FROM m.doctorContract.endDate) * 100 + EXTRACT(MONTH FROM m.doctorContract.endDate) >= 
                    EXTRACT(YEAR FROM CAST(:yearMonth AS date)) * 100 + EXTRACT(MONTH FROM CAST(:yearMonth AS date))
                )))
            """)
    Long findTotalAllowed(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("yearMonth") YearMonth yearMonth
    );
    
    @Query("""
                SELECT COALESCE(SUM(cma.amount), 0) 
                FROM MedicineWithQuantityDoctorV2 m 
                JOIN m.contractMedicineDoctorAmountV2s cma
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
                AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
                AND cma.yearMonth = :yearMonth
            """)
    Long findTotalWritten(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("yearMonth") YearMonth yearMonth
    );
    
    @Query("""
                SELECT COALESCE(SUM(cma.correction), 0) 
                FROM MedicineWithQuantityDoctorV2 m 
                JOIN m.contractMedicineDoctorAmountV2s cma
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
                AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
                AND cma.yearMonth = :yearMonth
            """)
    Long findTotalWrittenInFact(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("yearMonth") YearMonth yearMonth
    );
    
    @Query("""
                SELECT COALESCE(SUM(cma.amount), 0) 
                FROM MedicineWithQuantityDoctorV2 m 
                JOIN m.contractMedicineDoctorAmountV2s cma
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:field IS NULL OR m.doctorContract.doctor.fieldName = :field) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                AND (:workPlaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workPlaceId)
                AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalWrittenPrescriptions(
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workPlaceId") Long workPlaceId,
            @Param("field") Field field,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("""
                SELECT COALESCE(SUM(m.quote), 0) 
                FROM MedicineWithQuantityDoctorV2 m 
                WHERE 
                (:medicineId IS NULL OR m.medicine.id = :medicineId) 
                AND (:field IS NULL OR m.doctorContract.doctor.fieldName = :field) 
                AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                AND (:workPlaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workPlaceId)
                AND m.doctorContract.status='APPROVED'
                AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalQuotePrescriptions(
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workPlaceId") Long workPlaceId,
            @Param("field") Field field,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("""
                SELECT SUM(cma.amount)
                FROM MedicineWithQuantityDoctorV2 m
                JOIN m.contractMedicineDoctorAmountV2s cma
                WHERE 
                    (:medicineId IS NULL OR m.medicine.id= :medicineId)
                    AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType)
                    AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
                    AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
                    AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
                    AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
                    AND m.doctorContract.status='APPROVED'
                    AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
                    AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
            """)
    Long findTotalPrescriptionsByContractType(
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("contractType") ContractType contractType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("""
    SELECT new com.example.user_management_service.model.dto.SimpleDoctorPrescriptionDTO(
        m.medicine,
        m.doctorContract.doctor.userId,
        SUM(cma.amount)
    )
    FROM MedicineWithQuantityDoctorV2 m
    JOIN m.contractMedicineDoctorAmountV2s cma
    WHERE 
        (:medicineId IS NULL OR m.medicine.id = :medicineId)
        AND (:contractType IS NULL OR m.doctorContract.contractType = :contractType)
        AND (:districtId IS NULL OR m.doctorContract.doctor.district.id = :districtId)
        AND (:regionId IS NULL OR m.doctorContract.doctor.district.region.id = :regionId)
        AND (:workplaceId IS NULL OR m.doctorContract.doctor.workplace.id = :workplaceId)
        AND (:fieldName IS NULL OR m.doctorContract.doctor.fieldName = :fieldName)
        AND m.doctorContract.status = 'APPROVED'
        AND (CAST(:startDate AS date) IS NULL OR m.doctorContract.startDate >= :startDate)
        AND (CAST(:endDate AS date) IS NULL OR m.doctorContract.endDate <= :endDate)
    GROUP BY 
        m.medicine,
        m.doctorContract.doctor.userId
    HAVING 
        SUM(cma.amount) > 0
""")
    Page<SimpleDoctorPrescriptionDTO> findTotalPrescriptionsByDoctor(
            @Param("medicineId") Long medicineId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("contractType") ContractType contractType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
