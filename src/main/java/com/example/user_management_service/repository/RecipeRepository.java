package com.example.user_management_service.repository;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.PreparationType;
import com.example.user_management_service.model.Recipe;
import com.example.user_management_service.model.dto.ActiveDoctorSalesData;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

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

    @Query("""
    SELECT new com.example.user_management_service.model.dto.ActiveDoctorSalesData(
        SUM(p.quantity * m.cip), 
        FUNCTION('MONTH', r.dateCreation)
    )
    FROM Recipe r
    JOIN r.preparations p
    JOIN p.medicine m
    GROUP BY FUNCTION('MONTH', r.dateCreation)
    ORDER BY FUNCTION('MONTH', r.dateCreation)
""")
    List<ActiveDoctorSalesData> getMonthlySales();


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
            "AND (:doctorId IS NULL OR u.userId = :doctorId) " +
            "AND (u.role = 'DOCTOR') " +
            "ORDER BY r.dateCreation ASC")
    List<Recipe> findRecipesByFilters(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("district") String district,
            @Param("category") Field category,
            @Param("specialty") String specialty,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("doctorId") UUID doctorId,
            @Param("medicineId") Long medicineId);

    @Query("SELECT COUNT(r) FROM Recipe r " +
            "WHERE r.doctorId.userId = :doctorId " +
            "AND EXTRACT(YEAR FROM r.dateCreation) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM r.dateCreation) = EXTRACT(MONTH FROM CURRENT_DATE)")
    Integer countRecipesCreatedThisMonthByDoctor(@Param("doctorId") UUID doctorId);



    @Query(value = """
    SELECT COALESCE(AVG(recipe_count), 0) 
    FROM (
        SELECT COUNT(r.recipe_id) AS recipe_count 
        FROM recipes r 
        WHERE r.doctor_id = :doctorId 
        AND r.date_creation >= CURRENT_DATE - INTERVAL '12 months' 
        GROUP BY EXTRACT(YEAR FROM r.date_creation), EXTRACT(MONTH FROM r.date_creation)
    ) AS monthly_counts
    """, nativeQuery = true)
    Double averageRecipesLast12MonthsByDoctor(@Param("doctorId") UUID doctorId);

    @Query("SELECT COUNT(r) FROM Recipe r " +
            "WHERE r.doctorId IN (" +
            "   SELECT c.doctor FROM Contract c WHERE c.medAgent.userId = :medAgentId" +
            ") " +
            "AND EXTRACT(YEAR FROM r.dateCreation) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM r.dateCreation) = EXTRACT(MONTH FROM CURRENT_DATE)")
    Integer countRecipesByDoctorsAssignedByMedAgentThisMonth(@Param("medAgentId") UUID medAgentId);


    @Query("SELECT COUNT(r) FROM Recipe r " +
            "JOIN r.preparations p " +
            "WHERE r.doctorId IN (" +
            "   SELECT c.doctor FROM Contract c WHERE c.medAgent.userId = :medAgentId" +
            ") "+
            "AND EXTRACT(YEAR FROM r.dateCreation) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM r.dateCreation) = EXTRACT(MONTH FROM CURRENT_DATE)")
    Integer totalMedicineAmountByMedAgentThisMonth(@Param("medAgentId") UUID medAgentId);

    @Query("""
        SELECT r FROM Recipe r
        WHERE (:regionId IS NULL OR r.doctorId.district.region.id = :regionId)
        AND (:districtId IS NULL OR r.doctorId.district.id = :districtId)
        AND (:doctorField IS NULL OR r.doctorId.fieldName = :doctorField)
        AND (:doctorId IS NULL OR r.doctorId.userId = :doctorId)
        AND (:lastAnalysisFrom IS NULL OR r.dateCreation >= :lastAnalysisFrom)
        AND (:lastAnalysisTo IS NULL OR r.dateCreation <= :lastAnalysisTo)
         AND (
           (LOWER(r.doctorId.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
           OR (LOWER(r.doctorId.lastName) LIKE LOWER(CONCAT(:lastName, '%')))
           OR (LOWER(r.doctorId.middleName) LIKE LOWER(CONCAT(:middleName, '%')))
    )
        AND (:medicineId IS NULL OR r.recipeId IN (
                    SELECT rp.recipeId FROM Recipe rp JOIN rp.preparations p WHERE p.medicine.id = :medicineId
                ))
    """)
    Page<Recipe> findRecipesByFilters(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("medicineId") Long medicineId,
            @Param("doctorField") Field doctorField,
            @Param("lastAnalysisFrom") LocalDate lastAnalysisFrom,
            @Param("lastAnalysisTo") LocalDate lastAnalysisTo,
            @Param("doctorId") UUID doctorId,
            Pageable pageable
    );

}