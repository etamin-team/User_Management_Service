package com.example.user_management_service.repository;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.v2.MedicineWithQuantityDoctorV2; // Import V2 Entity
import com.example.user_management_service.model.v2.OutOfContractMedicineAmountV2; // Import V2 Entity
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:09 PM (GMT+5)
 */

@Repository
public interface MedicineRepository  extends JpaRepository<Medicine, Long> {

    @Query("SELECT MAX(id) FROM Medicine")
    Optional<Long> findMaxId();
    @Query("SELECT m FROM Medicine m JOIN m.mnn n WHERE n.id = :mnnId")
    List<Medicine> findByMnnId(@Param("mnnId") Long mnnId);

    @Query("SELECT m FROM Medicine m JOIN m.mnn i WHERE i.id IN :mnnIds ORDER BY m.name asc")
    List<Medicine> findByMnnIds(@Param("mnnIds") List<Long> mnnIds);

    @Query("SELECT m FROM Medicine m JOIN m.mnn i " +
            "WHERE i.id IN :mnnIds " +
            "GROUP BY m " +
            "HAVING COUNT(DISTINCT i.id) = :size "+
            "ORDER BY m.name asc ")
    List<Medicine> findByAllMnnIds(@Param("mnnIds") List<Long> mnnIds, @Param("size") long size);
    @Query("""
    SELECT u FROM Medicine u 
    WHERE u.status = 'ACTIVE'
    ORDER BY u.name ASC 
""")
    List<Medicine> findAllSortByCreatedDate();

    @Query("""
    SELECT u 
    FROM Medicine u 
    WHERE u.status = 'ACTIVE'
      AND (
        :name IS NULL 
        OR :name = '' 
        OR LOWER(u.name) LIKE LOWER(CONCAT(:name, '%')) 
        OR LOWER(u.nameRussian) LIKE LOWER(CONCAT(:name, '%')) 
        OR LOWER(u.nameUzLatin) LIKE LOWER(CONCAT(:name, '%'))
      )
    ORDER BY u.name ASC
""")
    List<Medicine> findAllSortByCreatedDate(@Param("name") String name);


    @Query("""
    SELECT u FROM Medicine u 
    WHERE u.status = 'ACTIVE'
    ORDER BY u.createdDate DESC
""")
    Page<Medicine> findAllSortByCreatedDatePageable(Pageable pageable);


    // --- Queries for V2 related entities ---
    // (Removed V1: findAgentGoalQuantitiesByMedicineId, findManagerGoalQuantitiesByMedicineId)

    @Query("SELECT m FROM MedicineWithQuantityDoctorV2 m WHERE m.medicine.id = :medicineId")
    List<MedicineWithQuantityDoctorV2> findWithQuantityDoctorV2ByMedicineId(@Param("medicineId") Long medicineId);

    @Query("SELECT m FROM OutOfContractMedicineAmountV2 m WHERE m.medicine.id = :medicineId")
    List<OutOfContractMedicineAmountV2> findOutOfContractV2ByMedicineId(@Param("medicineId") Long medicineId);


    // --- Existing queries for Sales, SalesReport, Recipe, Template (assuming V1/Common) ---
    @Query("SELECT s FROM Sales s WHERE s.medicine.id = :medicineId")
    List<Sales> findSalesByMedicineId(@Param("medicineId") Long medicineId);

    @Query("SELECT s FROM SalesReport s WHERE s.medicine.id = :medicineId")
    List<SalesReport> findSalesReportsByMedicineId(@Param("medicineId") Long medicineId);

    @Query("SELECT r FROM Recipe r WHERE EXISTS (SELECT p FROM r.preparations p WHERE p.medicine.id = :medicineId)")
    List<Recipe> findRecipesByMedicineId(@Param("medicineId") Long medicineId);

    @Query("SELECT t FROM Template t WHERE EXISTS (SELECT p FROM t.preparations p WHERE p.medicine.id = :medicineId)")
    List<Template> findTemplatesByMedicineId(@Param("medicineId") Long medicineId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM medicine_mnn WHERE medicine_id = :medicineId", nativeQuery = true)
    void deleteMedicineMnnReferences(@Param("medicineId") Long medicineId);

    @Query("SELECT m FROM Medicine m WHERE m.id IN :medicineIds")
    List<Medicine> findByIds(@Param("medicineIds") List<Long> medicineIds);
}