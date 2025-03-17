package com.example.user_management_service.repository;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.dto.DashboardDoctorsCoverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:37 PM (GMT+5)
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query("""
                SELECT new com.example.user_management_service.model.dto.DashboardDoctorsCoverage(
                    COALESCE((SELECT COUNT(u) FROM User u WHERE u.role = 'DOCTOR' and u.status= 'ACTIVE'), 0), 
                    COALESCE(COUNT(DISTINCT c.doctor.userId), 0), 
                    COALESCE(EXTRACT(MONTH FROM c.createdAt), 0)
                )
                FROM Contract c
                WHERE c.createdAt IS NOT NULL
                GROUP BY EXTRACT(MONTH FROM c.createdAt)
                ORDER BY EXTRACT(MONTH FROM c.createdAt)
            """)
    List<DashboardDoctorsCoverage> getDoctorsCoverage();


    @Query("SELECT c FROM Contract c WHERE c.status = :status ")
    Page<Contract> findByStatus(@Param("status") GoalStatus status, Pageable pageable);


    @Query("SELECT c FROM Contract c " +
            "WHERE c.doctor.userId = :doctorId " +
            "AND c.status = 'APPROVED'  order by c.id asc limit 1")
    Optional<Contract> findActiveContractByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT c FROM Contract c " +
            "WHERE c.doctor.userId = :doctorId " +
            "AND (c.status = 'APPROVED' OR c.status = 'PENDING_REVIEW') order by c.id asc limit 1")
    Optional<Contract> findActiveOrPendingContractByDoctorId(@Param("doctorId") UUID doctorId);


    @Modifying
    @Query("UPDATE Contract c SET c.status = :status WHERE c.id = :id")
    void updateContractStatus(@Param("id") UUID id, @Param("status") GoalStatus status);

    @Query("SELECT c FROM Contract c " +
            "WHERE c.medAgent.userId = :agentId")
    Page<Contract> findAllContractsByAgent(@Param("agentId") UUID agentId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT c.doctor) FROM Contract c WHERE c.medAgent.userId = :medAgentId")
    Integer countDoctorsByMedAgent(@Param("medAgentId") UUID medAgentId);

    @Query("SELECT COUNT(DISTINCT c.doctor) FROM Contract c " +
            "WHERE c.medAgent.userId = :medAgentId " +
            "AND EXTRACT(YEAR FROM c.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM c.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)")
    Integer countDoctorsByMedAgentThisMonth(@Param("medAgentId") UUID medAgentId);


    @Query("SELECT COUNT(c) FROM Contract c WHERE c.medAgent.userId = :medAgentId")
    Integer countContractsByMedAgent(@Param("medAgentId") UUID medAgentId);

    @Query("SELECT COUNT(c) FROM Contract c " +
            "WHERE c.medAgent.userId = :medAgentId " +
            "AND EXTRACT(YEAR FROM c.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM c.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)")
    Integer countContractsCreatedThisMonthByMedAgent(@Param("medAgentId") UUID medAgentId);

    @Query("SELECT c FROM Contract c WHERE c.medAgent.userId = :agentId")
    List<Contract> findAllByMedAgentId(@Param("agentId") UUID agentId);


    @Query("SELECT c FROM Contract c " +
            "JOIN c.medicineWithQuantityDoctors m " +
            "JOIN c.doctor d " +
            "WHERE (:query IS NULL OR " +
            "      LOWER(d.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(d.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(d.middleName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:medicineId IS NULL OR m.medicine.id = :medicineId) " +
            "AND (:regionId IS NULL OR d.district.region.id = :regionId) " +
            "AND (:districtId IS NULL OR d.district.id = :districtId) " +
            "AND (:workplaceId IS NULL OR d.workplace.id = :workplaceId) " +
            "AND (:fieldName IS NULL OR d.fieldName = :fieldName)")
    List<Contract> findContractsByFilters(@Param("medicineId") Long medicineId,
                                          @Param("query") String query,
                                          @Param("regionId") Long regionId,
                                          @Param("districtId") Long districtId,
                                          @Param("workplaceId") Long workplaceId,
                                          @Param("fieldName") Field fieldName);



    @Query("SELECT COALESCE(SUM(m.quote), 0) " +
            "FROM Contract c " +
            "JOIN c.medicineWithQuantityDoctors m " +
            "WHERE (:query IS NULL OR " +
            "      LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :query, '%')))  " +
            "AND (:medicineId IS NULL OR m.medicine.id = :medicineId) " +
            "AND (:districtId IS NULL OR c.doctor.district.id = :districtId) " +
            "AND (:regionId IS NULL OR c.doctor.district.region.id = :regionId) " +
            "AND (:workplaceId IS NULL OR c.doctor.workplace.id = :workplaceId) " +
            "AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName)")
    Long findTotalAllowed(@Param("medicineId") Long medicineId,
                          @Param("query") String query,
                          @Param("regionId") Long regionId,
                          @Param("districtId") Long districtId,
                          @Param("workplaceId") Long workplaceId,
                          @Param("fieldName") Field fieldName);

//    @Query("SELECT COALESCE(SUM(m.contractMedicineDoctorAmount.amount), 0) " +
//            "FROM Contract c " +
//            "JOIN c.medicineWithQuantityDoctors m " +
//            "JOIN c.doctor d " +
//            "WHERE (:query IS NULL OR " +
//            "      LOWER(d.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//            "      LOWER(d.lastName) LIKE LOWER(CONCAT('%', :query, '%')))  " +
//            "AND (:medicineId IS NULL OR m.medicine.id = :medicineId) " +
//            "AND (:districtId IS NULL OR d.district.id = :districtId) " +
//            "AND (:regionId IS NULL OR d.district.region.id = :regionId) " +
//            "AND (:workplaceId IS NULL OR d.workplace.id = :workplaceId) " +
//            "AND (:fieldName IS NULL OR d.fieldName = :fieldName)")
//    Long findTotalWritten(@Param("medicineId") Long medicineId,
//                          @Param("query") String query,
//                          @Param("regionId") Long regionId,
//                          @Param("districtId") Long districtId,
//                          @Param("workplaceId") Long workplaceId);

    @Query("SELECT COALESCE(SUM(cma.amount), 0) " +
            "FROM MedicineWithQuantityDoctor mwqd " +
            "JOIN mwqd.contractMedicineDoctorAmount cma " +
            "JOIN mwqd.doctorContract.doctor c " +
            "WHERE (:medicineId IS NULL OR mwqd.medicine.id = :medicineId) " +
            "AND (:query IS NULL OR " +
            "      LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')))  " +
            "AND (:districtId IS NULL OR c.district.id = :districtId) " +
            "AND (:regionId IS NULL OR c.district.region.id = :regionId) " +
            "AND (:workplaceId IS NULL OR c.workplace.id = :workplaceId) " +
            "AND (:fieldName IS NULL OR c.fieldName = :fieldName)")
    Long findTotalWritten(@Param("medicineId") Long medicineId,
                          @Param("query") String query,
                          @Param("regionId") Long regionId,
                          @Param("districtId") Long districtId,
                          @Param("workplaceId") Long workplaceId,
                          @Param("fieldName") Field fieldName);

//    @Query("SELECT COALESCE(SUM(cma.amount), 0) " +
//            "FROM ContractMedicineAmount cma " +
//            "WHERE EXISTS (SELECT 1 FROM MedicineWithQuantityDoctor mwqd " +
//            "              WHERE mwqd.contractMedicineDoctorAmount = cma " +
//            "              AND (:medicineId IS NULL OR mwqd.medicine.id = :medicineId))")
//    Long findTotalWritten(@Param("medicineId") Long medicineId);


    @Query("SELECT COALESCE((SELECT SUM(cma.amount) FROM ContractMedicineAmount cma " +
            "WHERE cma.id IN (SELECT mwqd.contractMedicineDoctorAmount.id FROM MedicineWithQuantityDoctor mwqd " +
            "WHERE (:medicineId IS NULL OR mwqd.medicine.id = :medicineId))), 0)")
    Long findTotalWritten(@Param("medicineId") Long medicineId);


    @Query("SELECT COALESCE(SUM(m.correction), 0) " +
            "FROM Contract c " +
            "JOIN c.medicineWithQuantityDoctors m " +
            "JOIN c.doctor d " +
            "WHERE (:query IS NULL OR " +
            "      LOWER(d.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(d.lastName) LIKE LOWER(CONCAT('%', :query, '%')))  " +
            "AND (:medicineId IS NULL OR m.medicine.id = :medicineId) " +
            "AND (:districtId IS NULL OR d.district.id = :districtId) " +
            "AND (:regionId IS NULL OR d.district.region.id = :regionId) " +
            "AND (:workplaceId IS NULL OR d.workplace.id = :workplaceId) " +
            "AND (:fieldName IS NULL OR d.fieldName = :fieldName)")
    Long findTotalWrittenInFact(@Param("medicineId") Long medicineId,
                                @Param("query") String query,
                                @Param("regionId") Long regionId,
                                @Param("districtId") Long districtId,
                                @Param("workplaceId") Long workplaceId,
                                @Param("fieldName") Field fieldName);


    @Query(""" 
        SELECT DISTINCT c FROM Contract c 
        LEFT JOIN c.medicineWithQuantityDoctors mwqd 
        WHERE (:regionId IS NULL OR c.doctor.district.region.id = :regionId) 
        AND (:districtId IS NULL OR c.doctor.district.id = :districtId) 
        AND (:workPlaceId IS NULL OR c.doctor.workplace.id = :workPlaceId) 
        AND (:fieldName IS NULL OR c.doctor.fieldName = :fieldName) 
        AND (:startDate IS NULL OR c.startDate >= :startDate) 
        AND (:endDate IS NULL OR c.endDate <= :endDate) 
        AND (:firstName IS NULL OR :firstName = '' OR LOWER(c.doctor.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) 
        AND (:lastName IS NULL OR :lastName = '' OR LOWER(c.doctor.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) 
        AND (:middleName IS NULL OR :middleName = '' OR LOWER(c.doctor.middleName) LIKE LOWER(CONCAT('%', :middleName, '%'))) 
""")
    Page<Contract> findContracts(@Param("regionId") Long regionId,
                                 @Param("districtId") Long districtId,
                                 @Param("workPlaceId") Long workPlaceId,
                                 @Param("firstName") String firstName,
                                 @Param("lastName") String lastName,
                                 @Param("middleName") String middleName,
                                 @Param("fieldName") Field fieldName,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 Pageable pageable);


}




