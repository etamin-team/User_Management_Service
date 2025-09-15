package com.example.user_management_service.repository;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.ContractTypeSalesData;
import com.example.user_management_service.model.dto.DashboardDoctorsCoverage;
import com.example.user_management_service.model.dto.RegionFieldDTO;
import com.example.user_management_service.model.dto.StatsEmployeeDTO;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-11/19/2024
 * By Sardor Tokhirov
 * Time-5:10 PM (GMT+5)
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.role = 'MEDAGENT' " +
            "AND u.district.id = :districtId " +
            "AND u.createdDate BETWEEN :startDate AND :endDate")
    List<User> findMedicalAgentsByDistrictAndMonth(@Param("districtId") Long districtId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' " +
            "AND u.district.region.id = :regionId " +
            "AND (:fieldName IS NULL OR u.fieldName = :fieldName )" +
            "AND u.createdDate BETWEEN :startDate AND :endDate")
    List<User> findCreatedDoctorsThisMonthByRegionAndMonth(@Param("regionId") Long regionId,
                                                           @Param("fieldName") Field fieldName,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' " +
            "AND u.district.id = :districtId " +
            "AND (:fieldName IS NULL OR u.fieldName = :fieldName )" +
            "AND u.createdDate BETWEEN :startDate AND :endDate")
    List<User> findCreatedDoctorsThisMonthByDistrictAndMonth(@Param("districtId") Long districtId,
                                                             @Param("fieldName") Field fieldName,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);






    @Query("""
                SELECT new com.example.user_management_service.model.dto.ContractTypeSalesData(
                    r.contractType, 
                    COUNT(r)
                )
                FROM Recipe r
                GROUP BY r.contractType
            """)
    List<ContractTypeSalesData> getRecipeCountByContractType();

    @Override
    Optional<User> findById(UUID uuid);

    Optional<User> findByNumber(String number);

    Optional<User> findByEmail(String email);
    @Query("""
        SELECT u FROM User u 
        WHERE u.role = :role
        AND (:creatorId IS NULL OR u.creatorId = :creatorId)
        AND (:regionId IS NULL OR u.district.region.id = :regionId)
        AND (:districtId IS NULL OR u.district.id = :districtId)
        AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
        AND (:fieldName IS NULL OR u.fieldName = :fieldName)
        AND (
               (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
               OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
               OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
        )
        AND u.status = 'ENABLED'
        """)
    Page<User> findUsersByFiltersPaginated(
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("fieldName") Field fieldName, Pageable pageable);


    @Query("""
        SELECT u FROM User u 
        WHERE u.role = :role
        AND (:creatorId IS NULL OR u.creatorId = :creatorId)
        AND ((:regionId IS NOT NULL AND u.district.region.id = :regionId)
            OR (:regionId IS NULL AND :regionIds IS NOT NULL AND u.district.region.id IN :regionIds))
        AND (:districtId IS NULL OR u.district.id = :districtId)
        AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
        AND (:fieldName IS NULL OR u.fieldName = :fieldName)
        AND (
               (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
               OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
               OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
        )
        AND u.status = 'ENABLED'
        """)
    Page<User> findUsersByFiltersPaginated(
            @Param("regionIds") List<Long> regionIds,
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("fieldName") Field fieldName, Pageable pageable);



    @Query("""
            SELECT u FROM User u 
            LEFT JOIN DoctorContractV2 c ON c.doctor.userId = u.userId
            LEFT JOIN c.medicineWithQuantityDoctorV2s mwqd
            LEFT JOIN mwqd.medicine m
            WHERE u.role = :role
            AND (:creatorId IS NULL OR u.creatorId = :creatorId)
            AND ((:regionId IS NOT NULL AND u.district.region.id = :regionId)
                 OR (:regionId IS NULL AND :regionIds IS NOT NULL AND u.district.region.id IN :regionIds))
            AND (:districtId IS NULL OR u.district.id = :districtId)
            AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
            AND (:fieldName IS NULL OR u.fieldName = :fieldName)
            AND (
                   (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                   OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                   OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            )
            AND u.status = 'ENABLED'
            AND (c.doctor.userId IS NULL OR c.doctor.userId = u.userId)
            AND  c.status = 'APPROVED'
            AND (:medicineId IS NULL OR m.id = :medicineId)
            """)
    Page<User> findUsersByFiltersPaginatedWithContracts(
            @Param("regionIds") List<Long> regionIds,
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("fieldName") Field fieldName,
            @Param("medicineId") Long medicineId,
            Pageable pageable);


    @Query("""
        SELECT u FROM User u 
        LEFT JOIN DoctorContractV2 c ON c.doctor.userId = u.userId
        LEFT JOIN c.medicineWithQuantityDoctorV2s mwqd
        LEFT JOIN mwqd.medicine m
        WHERE u.role = :role
        AND (:creatorId IS NULL OR u.creatorId = :creatorId)
        AND (:regionId IS NULL OR u.district.region.id = :regionId)
        AND (:districtId IS NULL OR u.district.id = :districtId)
        AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
        AND (:fieldName IS NULL OR u.fieldName = :fieldName)
        AND (
               (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
               OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
               OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
        )
        AND u.status = 'ENABLED'
        AND (c.doctor.userId IS NULL OR c.doctor.userId = u.userId)
        AND c.status = 'APPROVED'
        AND (:medicineId IS NULL OR m.id = :medicineId)
        """)
    Page<User> findUsersByFiltersPaginatedWithContracts(
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("fieldName") Field fieldName,
            @Param("medicineId") Long medicineId,
            Pageable pageable);

    @Query("SELECT new com.example.user_management_service.model.dto.StatsEmployeeDTO(" +
            "r.id, r.name, r.nameUzCyrillic, r.nameUzLatin, r.nameRussian, COUNT(u)) " +
            "FROM User u " +
            "JOIN u.district d " +
            "JOIN d.region r " +
            "WHERE u.role = 'MEDAGENT' " +
            "GROUP BY r.id, r.name, r.nameUzCyrillic, r.nameUzLatin, r.nameRussian")
    List<StatsEmployeeDTO> getUserCountByRegion();

    @Query("SELECT new com.example.user_management_service.model.dto.StatsEmployeeDTO(" +
            "d.id,d.name, d.nameUzCyrillic, d.nameUzLatin, d.nameRussian, COUNT(u)) " +
            "FROM User u " +
            "JOIN u.district d " +
            "JOIN d.region r " +
            "WHERE r.id = :regionId and u.role = 'MEDAGENT'" +
            "GROUP BY d.id, d.name, d.nameUzCyrillic, d.nameUzLatin, d.nameRussian")
    List<StatsEmployeeDTO> getUserCountByDistrictInRegion(@Param("regionId") Long regionId);


    // Get users by role
    List<User> findByRole(Role role);

    @Query("""
                SELECT count (u) FROM User u 
                WHERE u.role = :role
                AND (:regionId IS NULL OR u.district.region.id = :regionId)
                AND u.status = 'ENABLED'
            """)
    Long findByRoleAndRegionId(@Param("regionId") Long regionId,Role role);

    @Query("""
                SELECT u FROM User u 
                WHERE u.role = :role
                AND (:regionId IS NULL OR u.district.region.id = :regionId)
                AND u.status = 'ENABLED'
            """)
    Optional<User> findManagerByRoleAndRegionId(@Param("regionId") Long regionId,Role role);


    // Get all doctors
    default List<User> findDoctors() {
        return findByRole(Role.DOCTOR);
    }


    // Get all managers
    default List<User> findManagers() {
        return findByRole(Role.MANAGER);
    }


    @Query("SELECT u FROM User u WHERE u.workplace.id = :workPlaceId AND u.role = :role AND u.status = 'ENABLED' ")
    List<User> findDoctorsByWorkPlaceId(@Param("workPlaceId") Long workPlaceId, @Param("role") Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status ORDER BY u.createdDate DESC")
    Page<User> findDoctorsByStatus(@Param("role") Role role, @Param("status") UserStatus status, Pageable pageable);

    @Query("""
                SELECT u FROM User u 
                WHERE u.role = :role
                AND (:creatorId IS NULL OR u.creatorId = :creatorId)
                AND (:regionId IS NULL OR u.district.region.id = :regionId)
                AND (:districtId IS NULL OR u.district.id = :districtId)
                AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
                AND (:field IS NULL OR u.fieldName= :field)              
                AND (
                       (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                       OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                       OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            
                )
                AND u.status = 'ENABLED'
                ORDER BY u.firstName ASC 
            """)
    List<User> findUsersByFilters(
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("field") Field field
    );

    @Query("""
                SELECT u FROM User u 
                WHERE u.role = :role
                AND (:creatorId IS NULL OR u.creatorId = :creatorId)
                AND (:regionId IS NULL OR u.district.region.id = :regionId)
                AND (:districtId IS NULL OR u.district.id = :districtId)
                AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
                AND (:field IS NULL OR u.fieldName= :field)              
                AND (
                       (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                       OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                       OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            
                )
                AND u.status = 'ENABLED'
                ORDER BY u.firstName ASC 
            """)
    Page<User> findUsersByFilters(
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("field") Field field,
            Pageable pageable
    );


    @Query("""
                SELECT u FROM User u 
                WHERE u.role = :role
                AND (:creatorId IS NULL OR u.creatorId = :creatorId)
                AND ((:regionId IS NOT NULL AND u.district.region.id = :regionId)
                    OR (:regionId IS NULL AND :regionIds IS NOT NULL AND u.district.region.id IN :regionIds))
                AND (:districtId IS NULL OR u.district.id = :districtId)
                AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
                AND (:field IS NULL OR u.fieldName= :field)              
                AND (
                       (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                       OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                       OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            
                )
                AND u.status = 'ENABLED'
                ORDER BY u.firstName ASC 
            """)
    List<User> findUsersByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("field") Field field
    );


    @Query("""
                SELECT u FROM User u 
                WHERE u.role = :role
                AND (:creatorId IS NULL OR u.creatorId = :creatorId)
                AND ((:regionId IS NOT NULL AND u.district.region.id = :regionId)
                    OR (:regionId IS NULL AND :regionIds IS NOT NULL AND u.district.region.id IN :regionIds))
                AND (:districtId IS NULL OR u.district.id = :districtId)
                AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
                AND (:field IS NULL OR u.fieldName= :field)              
                AND (
                       (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                       OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                       OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            
                )
                AND u.status = 'ENABLED'
                ORDER BY u.firstName ASC 
            """)
    Page<User> findUsersByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("field") Field field,
            Pageable pageable
    );

    @Query("""
                    SELECT u FROM User u 
                    LEFT JOIN DoctorContractV2 c ON c.doctor.userId = u.userId
                    WHERE u.role = :role
                    AND (:creatorId IS NULL OR u.creatorId = :creatorId)
                    AND (:regionId IS NULL OR u.district.region.id = :regionId)
                    AND (:districtId IS NULL OR u.district.id = :districtId)
                    AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
                    AND (:field IS NULL OR u.fieldName= :field)              
                    AND (
                           (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                           OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                           OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            
                    )
                    AND u.status = 'ENABLED'
                    AND (c.doctor.userId IS NULL OR c.doctor.userId = u.userId)
                    AND ( c.status = 'APPROVED')
                    ORDER BY u.firstName ASC 
            """)
    List<User> findUsersByFiltersWitContracts(
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("field") Field field
    );

    @Query("""
                    SELECT u FROM User u 
                    LEFT JOIN DoctorContractV2 c ON c.doctor.userId = u.userId
                    WHERE u.role = :role
                    AND (:creatorId IS NULL OR u.creatorId = :creatorId)
                    AND ((:regionId IS NOT NULL AND u.district.region.id = :regionId) 
                     OR (:regionId IS NULL AND :regionIds IS NOT NULL AND u.district.region.id IN :regionIds))
                    AND (:districtId IS NULL OR u.district.id = :districtId)
                    AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
                    AND (:field IS NULL OR u.fieldName= :field)              
                    AND (
                           (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                           OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                           OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            
                    )
                    AND u.status = 'ENABLED'
                    AND (c.doctor.userId IS NULL OR c.doctor.userId = u.userId)
                    AND (c.status = 'APPROVED')
                    ORDER BY u.firstName ASC 
            """)
    List<User> findUsersByFiltersWitContracts(
            @Param("regionIds") List<Long> regionIds,
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("field") Field field
    );


    @Query("""
                SELECT COUNT(u) FROM User u 
                WHERE u.role = :role
                AND (:creatorId IS NULL OR u.creatorId = :creatorId)
                AND (:regionId IS NULL OR u.district.region.id = :regionId)
                AND (:districtId IS NULL OR u.district.id = :districtId)
                AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
                AND (
                       (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                       OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                       OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
                )
                AND u.status = 'ENABLED'
            """)
    Long countDoctorsByFilters(
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName
    );




    @Query("""
                SELECT COUNT(w) FROM User w 
                WHERE w.district.region.id = :regionId and w.role = 'DOCTOR' AND w.status = 'ENABLED'
            """)
    Long countByRegionId(@Param("regionId") Long regionId);

    @Query("""
                SELECT w FROM User w 
                WHERE w.district.region.id = :regionId and w.role = 'MANAGER' AND w.status = 'ENABLED' order by w.createdDate asc  limit 1
            """)
    Optional<User> getManagerByRegionId(@Param("regionId") Long regionId);

    @Query("""
                SELECT COUNT(w) FROM User w 
                WHERE w.district.region.id = :regionId and w.role = 'DOCTOR' AND w.status = 'ENABLED'
            """)
    Long countByRegionIdInFact(@Param("regionId") Long regionId);

    @Query("""
                SELECT COUNT(w) FROM User w 
                WHERE w.district.id = :districtId and w.role = 'DOCTOR'
            """)
    Long countByDistrictId(@Param("districtId") Long districtId);

    @Query("""
                SELECT COUNT(w) FROM User w 
                WHERE w.district.id = :districtId and w.role = 'DOCTOR' AND w.status = 'ENABLED'
            """)
    Long countByDistrictIdInFact(@Param("districtId") Long districtId);


    @Query("""
                SELECT NEW com.example.user_management_service.model.dto.RegionFieldDTO(u.fieldName, COUNT(u)) FROM User u 
                WHERE u.district.id = :districtId
                GROUP BY u.fieldName
            """)
    List<RegionFieldDTO> countUsersByFieldAndDistrict(@Param("districtId") Long districtId);

    @Query("""
                SELECT NEW com.example.user_management_service.model.dto.RegionFieldDTO(u.fieldName, COUNT(u)) FROM User u  
                WHERE u.district.region.id = :regionId
                GROUP BY u.fieldName
            """)
    List<RegionFieldDTO> countUsersByFieldAndRegion(@Param("regionId") Long regionId);


    @Query("""
                SELECT NEW com.example.user_management_service.model.dto.RegionFieldDTO(u.fieldName, COUNT(u))  FROM User u 
                GROUP BY u.fieldName
            """)
    List<RegionFieldDTO> countUsersByFieldAndRegion();

    @Query("""
                SELECT NEW com.example.user_management_service.model.dto.RegionFieldDTO(u.fieldName, COUNT(u)) FROM User u 
                WHERE u.workplace.id = :workplaceId
                GROUP BY u.fieldName
            """)
    List<RegionFieldDTO> countUsersByFieldAndWorkplace(@Param("workplaceId") Long workplaceId);
    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.role = :role " +
            "AND (:creatorId IS NULL OR u.creatorId = :creatorId) " +
            "AND (:regionId IS NULL OR u.district.region.id = :regionId) " +
            "AND (:districtId IS NULL OR u.district.id = :districtId) " +
            "AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId) " +
            "AND (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%'))) " +
            "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) " +
            "AND (:middleName IS NULL OR LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%'))) " +
            "AND u.status = 'ENABLED' " +
            "AND YEAR(u.createdDate) = YEAR(CURRENT_DATE) " +
            "AND MONTH(u.createdDate) = MONTH(CURRENT_DATE)")
    long countUsersCreatedThisMonth(
            @Param("role") Role role,
            @Param("creatorId") String creatorId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName
    );




}