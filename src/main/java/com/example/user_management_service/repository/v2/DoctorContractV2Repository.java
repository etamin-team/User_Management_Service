package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.dto.DashboardDoctorsCoverage;
import com.example.user_management_service.model.v2.DoctorContractV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query("""
            SELECT COALESCE(SUM(mwd.quote), 0)
            FROM DoctorContractV2 c
            JOIN c.medicineWithQuantityDoctorV2s mwd
            WHERE c.startDate BETWEEN :startDate AND :endDate
        """)
    Long getTotalContractQuotesBetweenDates(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT new com.example.user_management_service.model.dto.DashboardDoctorsCoverage(
                    COALESCE((SELECT COUNT(u) FROM User u WHERE u.role = 'DOCTOR' and u.status= 'ENABLED'), 0),
                    COALESCE(COUNT(DISTINCT c.doctor.userId), 0),
                    COALESCE(EXTRACT(MONTH FROM c.createdAt), 0)
                )
                FROM DoctorContractV2 c
                WHERE c.createdAt IS NOT NULL
                GROUP BY EXTRACT(MONTH FROM c.createdAt)
                ORDER BY EXTRACT(MONTH FROM c.createdAt)
            """)
    List<DashboardDoctorsCoverage> getDoctorsCoverage();

    @Query("""
            SELECT c FROM DoctorContractV2 c
            WHERE c.createdBy.userId = :agentId
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workPlaceId IS NULL OR c.doctor.workplace.id = :workPlaceId)
            AND (:firstName IS NULL OR :firstName = '' OR LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
            AND (:lastName IS NULL OR :lastName = '' OR LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
            AND (:middleName IS NULL OR :middleName = '' OR LOWER(c.doctor.middleName) LIKE LOWER(CONCAT('%', :middleName, '%')))
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            """)
    Page<DoctorContractV2> findAllContractsByAgent(
            @Param("agentId") UUID agentId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workPlaceId") Long workPlaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("fieldName") Field fieldName,
            Pageable pageable
    );

    // --- NEW FILTERED CONTRACT METHODS FOR V2 ---
    @Query("""
            SELECT DISTINCT c FROM DoctorContractV2 c
            WHERE (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workPlaceId IS NULL OR c.doctor.workplace.id = :workPlaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (:nameQuery IS NULL OR :nameQuery = '' OR
                LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :nameQuery, '%')) OR
                LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :nameQuery, '%')) OR
                LOWER(c.doctor.middleName) LIKE LOWER(CONCAT('%', :nameQuery, '%'))
            )
            AND (:medicineId IS NULL OR EXISTS (
                SELECT 1 FROM c.medicineWithQuantityDoctorV2s m WHERE m.medicine.id = :medicineId
            ))
            AND (CAST(:startDate AS date) IS NULL OR c.createdAt >= :startDate)
            AND (CAST(:endDate AS date) IS NULL OR c.createdAt <= :endDate)
            """)
    Page<DoctorContractV2> findFilteredContracts(
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workPlaceId") Long workPlaceId,
            @Param("nameQuery") String nameQuery, // Simplified to one query param for name
            @Param("fieldName") Field fieldName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("medicineId") Long medicineId,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT c FROM DoctorContractV2 c
            WHERE (:regionIds IS NULL OR c.doctor.district.region.id IN :regionIds)
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workPlaceId IS NULL OR c.doctor.workplace.id = :workPlaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (:nameQuery IS NULL OR :nameQuery = '' OR
                LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :nameQuery, '%')) OR
                LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :nameQuery, '%')) OR
                LOWER(c.doctor.middleName) LIKE LOWER(CONCAT('%', :nameQuery, '%'))
            )
            AND (:medicineId IS NULL OR EXISTS (
                SELECT 1 FROM c.medicineWithQuantityDoctorV2s m WHERE m.medicine.id = :medicineId
            ))
            AND (CAST(:startDate AS date) IS NULL OR c.createdAt >= :startDate)
            AND (CAST(:endDate AS date) IS NULL OR c.createdAt <= :endDate)
            """)
    Page<DoctorContractV2> findFilteredContracts(
            @Param("regionIds") List<Long> regionIds,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workPlaceId") Long workPlaceId,
            @Param("nameQuery") String nameQuery, // Simplified to one query param for name
            @Param("fieldName") Field fieldName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("medicineId") Long medicineId,
            Pageable pageable
    );
    // --- END NEW FILTERED CONTRACT METHODS FOR V2 ---

    @Query("SELECT c FROM DoctorContractV2 c " +
            "WHERE c.doctor.userId = :doctorId " +
            "AND (c.status = 'APPROVED' OR c.status = 'PENDING_REVIEW') order by c.id asc limit 1")
    Optional<DoctorContractV2> findActiveOrPendingContractByDoctorId(@Param("doctorId") UUID doctorId);


    @Query("SELECT c FROM DoctorContractV2 c WHERE c.status = :status AND (:regionIds IS NULL OR c.doctor.district.region.id IN :regionIds) ")
    Page<DoctorContractV2> findByStatus(@Param("regionIds") List<Long> regionIds,@Param("status") GoalStatus status, Pageable pageable);


    @Query("SELECT c FROM DoctorContractV2 c " +
            "WHERE c.doctor.userId = :doctorId " +
            "AND c.status = 'APPROVED'  order by c.id asc limit 1")
    Optional<DoctorContractV2> findActiveContractByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT c FROM DoctorContractV2 c WHERE c.status = :status")
    Page<DoctorContractV2> findByStatus(@Param("status") GoalStatus status, Pageable pageable);

    @Query("SELECT d FROM DoctorContractV2 d WHERE d.doctor.userId = :doctorId " +
            "AND d.status = 'PENDING_REVIEW' " +
            "AND EXISTS (SELECT m FROM d.medicineWithQuantityDoctorV2s mwq JOIN mwq.contractMedicineDoctorAmountV2s m " +
            "WHERE m.yearMonth = :yearMonth)")
    Optional<DoctorContractV2> getContractsByDoctorId(@Param("doctorId") UUID doctorId,
                                                      @Param("yearMonth") YearMonth yearMonth);

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
            "AND c.createdAt BETWEEN :yearMonth AND :yearMonth")
    Long countByCreatedByAndDistrictAndCreatedBetween(@Param("createdBy") UUID createdBy, @Param("districtId") Long districtId,
                                                      @Param("yearMonth") YearMonth yearMonth);

    @Query("SELECT dcv FROM DoctorContractV2 dcv WHERE dcv.createdAt BETWEEN :yearMonth AND :yearMonth")
    List<DoctorContractV2> findByCreatedThisMonth(@Param("yearMonth") YearMonth yearMonth);

    @Query("""
            SELECT c FROM DoctorContractV2 c
            JOIN c.medicineWithQuantityDoctorV2s m
            JOIN m.contractMedicineDoctorAmountV2s cm
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
            AND (:yearMonth IS NULL OR cm.yearMonth = :yearMonth)
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
            JOIN m.contractMedicineDoctorAmountV2s cm
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
            AND (:yearMonth IS NULL OR cm.yearMonth = :yearMonth)
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
            @Param("yearMonth") YearMonth yearMonth
    );

    @Query("""
            SELECT c FROM DoctorContractV2 c
            JOIN c.medicineWithQuantityDoctorV2s m
            JOIN m.contractMedicineDoctorAmountV2s cm
            WHERE
            (:medicineId IS NULL OR m.medicine.id = :medicineId)
            AND (:contractType IS NULL OR c.contractType = :contractType)
            AND (CAST(:query AS string) IS NULL OR
                 LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
                 LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:regionIds IS NULL OR c.doctor.district.region.id IN :regionIds)
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (:yearMonth IS NULL OR cm.yearMonth = :yearMonth)
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
            JOIN m.contractMedicineDoctorAmountV2s cm
            WHERE
            (:medicineId IS NULL OR m.medicine.id = :medicineId)
            AND (:contractType IS NULL OR c.contractType = :contractType)
            AND (CAST(:query AS string) IS NULL OR
                 LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
                 LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:regionIds IS NULL OR c.doctor.district.region.id IN :regionIds)
            AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
            AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
            AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId)
            AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
            AND (:yearMonth IS NULL OR cm.yearMonth = :yearMonth)
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
            @Param("yearMonth") YearMonth yearMonth
    );

    @Query("SELECT COUNT(c) FROM DoctorContractV2 c WHERE c.status = :status")
    Long countByStatus(@Param("status") GoalStatus status);

    @Query("""
                SELECT COUNT(DISTINCT c.doctor.userId) FROM DoctorContractV2 c
                WHERE c.status = 'APPROVED'
                AND (:creatorId IS NULL OR c.createdBy.userId = CAST(:creatorId AS uuid))
                AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId)
                AND (:districtId IS NULL OR c.doctor.district.id = :districtId)
                AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId)
                AND (
                       (:name1 IS NULL OR :name1 = '' OR LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :name1, '%')) OR LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :name1, '%')) OR LOWER(c.doctor.middleName) LIKE LOWER(CONCAT('%', :name1, '%')))
                       AND (:name2 IS NULL OR :name2 = '' OR LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :name2, '%')) OR LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :name2, '%')) OR LOWER(c.doctor.middleName) LIKE LOWER(CONCAT('%', :name2, '%')))
                       AND (:name3 IS NULL OR :name3 = '' OR LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :name3, '%')) OR LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :name3, '%')) OR LOWER(c.doctor.middleName) LIKE LOWER(CONCAT('%', :name3, '%')))
                )
                AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)
                AND c.doctor.status = 'ACTIVE'
            """)
    Long countDoctorsWithApprovedContracts(
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("name1") String name1,
            @Param("name2") String name2,
            @Param("name3") String name3,
            @Param("fieldName") Field fieldName
    );

}