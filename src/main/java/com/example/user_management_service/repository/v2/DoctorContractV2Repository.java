package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.v2.DoctorContractV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-9/11/2025
 * By Sardor Tokhirov
 * Time-2:28 AM (GMT+5)
 */
@Repository
public interface DoctorContractV2Repository extends JpaRepository<DoctorContractV2, Long> {

    @Query("SELECT c FROM DoctorContractV2 c WHERE c.status = :status ")
    Page<DoctorContractV2> findByStatus(@Param("status") GoalStatus status, Pageable pageable);


    @Query("SELECT d FROM DoctorContractV2 d WHERE d.doctor.userId = :doctorId " +
            "AND d.status = 'PENDING_REVIEW' " +
            "AND (d.endDate IS NULL OR d.endDate >= :currentDate)")
    Optional<DoctorContractV2> getContractsByDoctorId(@Param("doctorId") UUID doctorId,
                                                      @Param("currentDate") LocalDate currentDate);

    @Query("SELECT d FROM DoctorContractV2 d WHERE d.doctor.userId = :doctorId " +
            "AND d.status = 'APPROVED'")
    Optional<DoctorContractV2> findByDoctorUserId(@Param("doctorId") UUID doctorId);


    @Query("SELECT c FROM DoctorContractV2 c WHERE c.doctor.district.region.id = :regionId")
    List<DoctorContractV2> findByRegion(@Param("regionId") Long regionId);

    @Query("SELECT c FROM DoctorContractV2 c WHERE c.createdBy.userId = :agentId AND c.doctor.district.id = :districtId")
    List<DoctorContractV2> findByCreatedByAndDistrict(@Param("agentId") UUID agentId, @Param("districtId") Long districtId);



    @Query("SELECT COUNT(c) FROM DoctorContractV2 c WHERE c.createdBy.userId = :createdBy AND c.doctor.district.id = :districtId")
    Long countByCreatedByAndDistrict(@Param("createdBy") UUID createdBy, @Param("districtId") Long districtId);

    @Query("SELECT COUNT(c) FROM DoctorContractV2 c WHERE c.createdBy.userId = :createdBy AND c.doctor.district.id = :districtId " +
            "AND c.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedByAndDistrictAndCreatedBetween(@Param("createdBy") UUID createdBy, @Param("districtId") Long districtId,
                                                      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



    @Query("SELECT dcv FROM DoctorContractV2 dcv WHERE dcv.createdAt BETWEEN :startDate AND :endDate")
    List<DoctorContractV2> findByCreatedThisMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("""
            SELECT c FROM DoctorContractV2 c 
            JOIN c.medicineWithQuantityDoctorV2s m
            WHERE 
            (:medicineId IS NULL OR m.medicine.id = :medicineId)
            AND (:contractType IS NULL OR c.contractType = :contractType)
            AND (CAST(:query AS string) IS NULL OR 
                 LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR 
                 LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (CAST(:yearMonth AS date) IS NULL OR (
                    EXTRACT(YEAR FROM c.startDate) * 100 + EXTRACT(MONTH FROM c.startDate) <= 
                    EXTRACT(YEAR FROM CAST(:yearMonth AS date)) * 100 + EXTRACT(MONTH FROM CAST(:yearMonth AS date)) AND 
                    (c.endDate IS NULL OR 
                    EXTRACT(YEAR FROM c.endDate) * 100 + EXTRACT(MONTH FROM c.endDate) >= 
                    EXTRACT(YEAR FROM CAST(:yearMonth AS date)) * 100 + EXTRACT(MONTH FROM CAST(:yearMonth AS date))
                )))
            GROUP BY c
    """)
    List<DoctorContractV2> findContractsByFilters(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("query") String query,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("yearMonth") YearMonth yearMonth
    );
    
    @Query("""
            SELECT c FROM DoctorContractV2 c 
            JOIN c.medicineWithQuantityDoctorV2s m
            WHERE 
            (:medicineId IS NULL OR m.medicine.id = :medicineId)
            AND (:contractType IS NULL OR c.contractType = :contractType)
            AND (CAST(:query AS string) IS NULL OR 
                 LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR 
                 LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (CAST(:startDate AS date) IS NULL OR c.startDate >= :startDate)
            AND (CAST(:endDate AS date) IS NULL OR c.endDate <= :endDate)
            GROUP BY c
    """)
    List<DoctorContractV2> findContractsByDateRangeFilters(
            @Param("medicineId") Long medicineId,
            @Param("contractType") ContractType contractType,
            @Param("query") String query,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("""
            SELECT c FROM DoctorContractV2 c 
            JOIN c.medicineWithQuantityDoctorV2s m
            WHERE 
            (:medicineId IS NULL OR m.medicine.id = :medicineId)
            AND (:contractType IS NULL OR c.contractType = :contractType)
            AND (CAST(:query AS string) IS NULL OR 
                 LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR 
                 LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
            AND c.doctor.district.region.id IN :regionIds
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (CAST(:yearMonth AS date) IS NULL OR (
                    EXTRACT(YEAR FROM c.startDate) * 100 + EXTRACT(MONTH FROM c.startDate) <= 
                    EXTRACT(YEAR FROM CAST(:yearMonth AS date)) * 100 + EXTRACT(MONTH FROM CAST(:yearMonth AS date)) AND 
                    (c.endDate IS NULL OR 
                    EXTRACT(YEAR FROM c.endDate) * 100 + EXTRACT(MONTH FROM c.endDate) >= 
                    EXTRACT(YEAR FROM CAST(:yearMonth AS date)) * 100 + EXTRACT(MONTH FROM CAST(:yearMonth AS date))
                )))
            GROUP BY c
    """)
    List<DoctorContractV2> findContractsByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("contractType") ContractType contractType,
            @Param("medicineId") Long medicineId,
            @Param("query") String query,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("yearMonth") YearMonth yearMonth
    );
    
    @Query("""
            SELECT c FROM DoctorContractV2 c 
            JOIN c.medicineWithQuantityDoctorV2s m
            WHERE 
            (:medicineId IS NULL OR m.medicine.id = :medicineId)
            AND (:contractType IS NULL OR c.contractType = :contractType)
            AND (CAST(:query AS string) IS NULL OR 
                 LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR 
                 LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
            AND c.doctor.district.region.id IN :regionIds
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (CAST(:startDate AS date) IS NULL OR c.startDate >= :startDate)
            AND (CAST(:endDate AS date) IS NULL OR c.endDate <= :endDate)
            GROUP BY c
    """)
    List<DoctorContractV2> findContractsByDateRangeFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("contractType") ContractType contractType,
            @Param("medicineId") Long medicineId,
            @Param("query") String query,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") Field fieldName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COUNT(c) FROM DoctorContractV2 c WHERE c.status = :status")
    Long countByStatus(@Param("status") GoalStatus status);
}