package com.example.user_management_service.repository;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.PreparationType;
import com.example.user_management_service.model.Recipe;
import com.example.user_management_service.model.dto.ActiveDoctorSalesData;
import com.example.user_management_service.model.dto.ContractTypeSalesData;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    SELECT new com.example.user_management_service.model.dto.ContractTypeSalesData(
        r.contractType, 
        COUNT(r.recipeId)
    )
    FROM Recipe r 
    WHERE r.dateCreation >= DATE_TRUNC('month', CURRENT_DATE)
    GROUP BY r.contractType
""")
    List<ContractTypeSalesData> getTotalSoldByContractType();



//    @Query("SELECT COUNT(r) FROM Recipe r " +
//            "WHERE (:query IS NULL OR " +
//            "      LOWER(r.doctorId.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//            "      LOWER(r.doctorId.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
//            "AND (:medicineId IS NULL OR EXISTS (SELECT 1 FROM r.preparations p WHERE p.medicine.id = :medicineId)) " +
//            "AND (:districtId IS NULL OR r.doctorId.district.id = :districtId) " +
//            "AND (:regionId IS NULL OR r.doctorId.district.region.id = :regionId) " +
//            "AND (:fieldName IS NULL OR r.doctorId.fieldName = :fieldName)")
//    Long countRecipesByFilters(@Param("medicineId") Long medicineId,
//                               @Param("query") String query,
//                               @Param("regionId") Long regionId,
//                               @Param("districtId") Long districtId,
//                               @Param("fieldName") Field fieldName);

//    @Query("SELECT COUNT(DISTINCT r) FROM Recipe r JOIN r.preparations p WHERE p.medicine.id = :medicineId")
//    long countByMedicineId(@Param("medicineId") Long medicineId);


    @Query("SELECT COUNT(r) FROM Recipe r JOIN r.preparations p " +
            "WHERE p.medicine.id = :medicineId " +
            "AND (:districtId IS NULL OR r.doctorId.district.id = :districtId) " +
            "AND (:regionId IS NULL OR r.doctorId.district.region.id = :regionId) " +
            "AND (:fieldName IS NULL OR r.doctorId.fieldName = :fieldName) " +
            "AND (:query IS NULL OR " +
            "      LOWER(r.doctorId.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "      LOWER(r.doctorId.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    long countByMedicineIdAndFilters(@Param("medicineId") Long medicineId,
                                     @Param("query") String query,
                                     @Param("regionId") Long regionId,
                                     @Param("districtId") Long districtId,
                                     @Param("fieldName") Field fieldName);



    @Query("""
    SELECT new com.example.user_management_service.model.dto.ActiveDoctorSalesData(
        SUM(p.quantity * m.cip), 
        EXTRACT(MONTH FROM r.dateCreation)
    )
    FROM Recipe r
    JOIN r.preparations p
    JOIN p.medicine m
    GROUP BY EXTRACT(MONTH FROM r.dateCreation)
    ORDER BY EXTRACT(MONTH FROM r.dateCreation)
""")
    List<ActiveDoctorSalesData> getMonthlySales();



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
                WHERE (:districtId IS NULL OR r.doctorId.district.id = :districtId)
                AND ((:regionId IS NOT NULL AND r.doctorId.district.region.id = :regionId)
                    OR (:regionId IS NULL AND :regionIds IS NOT NULL AND r.doctorId.district.region.id IN :regionIds))  
                AND (:doctorField IS NULL OR r.doctorId.fieldName = :doctorField)
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
                ORDER BY r.dateCreation DESC
            """)
    Page<Recipe> findRecipesByFilters(
            @Param("regionIds") List<Long> regionIds,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("medicineId") Long medicineId,
            @Param("doctorField") Field doctorField,
            @Param("lastAnalysisFrom") LocalDate lastAnalysisFrom,
            @Param("lastAnalysisTo") LocalDate lastAnalysisTo,
            Pageable pageable
    );

    @Query("""
        SELECT r FROM Recipe r
        WHERE (:regionId IS NULL OR r.doctorId.district.region.id = :regionId)
        AND (:districtId IS NULL OR r.doctorId.district.id = :districtId)
        AND (:doctorField IS NULL OR r.doctorId.fieldName = :doctorField)
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
        ORDER BY r.dateCreation DESC
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
            Pageable pageable
    );
    @Query("""
        SELECT r FROM Recipe r
        WHERE (:regionId IS NULL OR r.doctorId.district.region.id = :regionId)
        AND (:districtId IS NULL OR r.doctorId.district.id = :districtId)
        AND (:doctorField IS NULL OR r.doctorId.fieldName = :doctorField)
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
        ORDER BY r.dateCreation DESC
    """)
    List<Recipe> findRecipesByFilters(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("medicineId") Long medicineId,
            @Param("doctorField") Field doctorField,
            @Param("lastAnalysisFrom") LocalDate lastAnalysisFrom,
            @Param("lastAnalysisTo") LocalDate lastAnalysisTo
    );

    @Query("""
    SELECT SUM(m.cip)
    FROM Recipe r
    JOIN r.preparations p
    JOIN p.medicine m
    WHERE CAST(r.dateCreation AS date) BETWEEN CAST(:startDate AS date) AND CAST(:endDate AS date)
    AND r.doctorId.userId = :doctorId
""")
    Long getTotalPriceBetweenDatesAndDoctor(@Param("doctorId") UUID doctorId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("""
    SELECT SUM(m.cip)
    FROM Recipe r
    JOIN r.preparations p
    JOIN p.medicine m
    WHERE CAST(r.dateCreation AS date) BETWEEN CAST(:startDate AS date) AND CAST(:endDate AS date)
    
""")
    Long getTotalPriceBetweenDatesAndDoctor(
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);




}