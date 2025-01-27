package com.example.user_management_service.repository;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.PreparationType;
import com.example.user_management_service.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-10:51 PM (GMT+5)
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("SELECT DISTINCT r FROM Recipe r " +
            "JOIN r.preparations p " +
            "JOIN r.doctorId u " +
            "WHERE (:firstName IS NULL OR u.firstName LIKE %:firstName%) " +
            "AND (:middleName IS NULL OR u.middleName LIKE %:middleName%) " +
            "AND (:lastName IS NULL OR u.lastName LIKE %:lastName%) " +
            "AND (:district IS NULL OR u.district.name LIKE %:district%) " +
            "AND (:category IS NULL OR u.fieldName = :category) " +
            "AND (:specialty IS NULL OR u.position LIKE %:specialty%) " +
            "AND (:startDate IS NULL OR r.dateCreation >= :startDate) " +
            "AND (:endDate IS NULL OR r.dateCreation <= :endDate) " +
            "AND (:medicineId IS NULL OR p.medicine.id = :medicineId) " +
            "AND (u.role = 'DOCTOR')")  // Filter by doctor role
    List<Recipe> findRecipesByFilters(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("district") String district,
            @Param("category") Field category,
            @Param("specialty") String specialty,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("medicineId") Long medicineId);


}