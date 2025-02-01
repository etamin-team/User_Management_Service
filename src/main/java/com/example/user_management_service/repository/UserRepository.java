package com.example.user_management_service.repository;

import com.example.user_management_service.model.User;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

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

    @Override
    Optional<User> findById(UUID uuid);

    Optional<User> findByNumber(String number);

    Optional<User> findByEmail(String email);

    // Get users by creator ID
    List<User> findByCreatorId(String creatorId);

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



    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status ORDER BY u.createdDate DESC")
    Page<User> findDoctorsByStatus(@Param("role") Role role, @Param("status") UserStatus status, Pageable pageable);

    @Query("""
    SELECT u FROM User u 
    WHERE u.role = :role
    AND (:creatorId IS NULL OR u.creatorId = :creatorId)
    AND (:regionId IS NULL OR u.district.region.id = :regionId)
    AND (:cityId IS NULL OR u.district.id = :cityId)
    AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)
    AND (
        (:firstName IS NULL AND :lastName IS NULL AND :middleName IS NULL) 
        OR (
            (:firstName IS NULL OR u.firstName ILIKE  :firstName)
            AND (:lastName IS NULL OR u.lastName ILIKE  :lastName)
            AND (:middleName IS NULL OR u.middleName ILIKE :middleName)
        )
    )
""")
    List<User> findUsersByFilters(
            @Param("role") Role role,
            @Param("creatorId") UUID creatorId,
            @Param("regionId") Long regionId,
            @Param("cityId") Long cityId,
            @Param("workplaceId") Long workplaceId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName
    );



}