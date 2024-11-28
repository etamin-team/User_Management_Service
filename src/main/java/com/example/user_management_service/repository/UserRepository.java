package com.example.user_management_service.repository;

import com.example.user_management_service.model.User;
import com.example.user_management_service.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Get users with multiple filters including role
    List<User> findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
            Role role, String creatorId, Long countryId, Long regionId, Long workplaceId);

    boolean existsByRole(Role role);

    // Search doctors by name (first or last name)
    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.firstName LIKE %:query% OR u.lastName LIKE %:query%)")
    List<User> searchDoctorsByName(Role role, String query);

    // Get all doctors
    default List<User> findDoctors() {
        return findByRole(Role.DOCTOR);
    }

    // Get all admins
    default List<User> findAdmins() {
        return findByRole(Role.ADMIN);
    }

    // Get all managers
    default List<User> findManagers() {
        return findByRole(Role.MANAGER);
    }

    // Search managers by name (first or last name)
    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.firstName LIKE %:query% OR u.lastName LIKE %:query%)")
    List<User> searchManagersByName(Role role, String query);
}