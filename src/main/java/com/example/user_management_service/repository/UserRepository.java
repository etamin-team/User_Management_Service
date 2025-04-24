package com.example.user_management_service.repository;

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

import java.time.LocalDateTime;
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
            Pageable pageable
    );


    @Query("SELECT new com.example.user_management_service.model.dto.StatsEmployeeDTO(" +
            "r.id, r.name, r.nameUzCyrillic, r.nameUzLatin, r.nameRussian, COUNT(u)) " +
            "FROM User u " +
            "JOIN u.district d " +
            "JOIN d.region r " +
            "GROUP BY r.id, r.name, r.nameUzCyrillic, r.nameUzLatin, r.nameRussian")
    List<StatsEmployeeDTO> getUserCountByRegion();

    @Query("SELECT new com.example.user_management_service.model.dto.StatsEmployeeDTO(" +
            "d.id,d.name, d.nameUzCyrillic, d.nameUzLatin, d.nameRussian, COUNT(u)) " +
            "FROM User u " +
            "JOIN u.district d " +
            "JOIN d.region r " +
            "WHERE r.id = :regionId " +
            "GROUP BY d.id, d.name, d.nameUzCyrillic, d.nameUzLatin, d.nameRussian")
    List<StatsEmployeeDTO> getUserCountByDistrictInRegion(@Param("regionId") Long regionId);


    // Get users by role
    List<User> findByRole(Role role);


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
                AND (
                       (LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                       OR (LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
                       OR (LOWER(u.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
            
                )
                AND u.status = 'ENABLED'
                ORDER BY u.createdDate DESC
            """)
    List<User> findUsersByFilters(
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