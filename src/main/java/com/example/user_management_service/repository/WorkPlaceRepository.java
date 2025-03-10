package com.example.user_management_service.repository;

import com.example.user_management_service.model.MedicalInstitutionType;
import com.example.user_management_service.model.WorkPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-12:17 PM (GMT+5)
 */
@Repository
public interface WorkPlaceRepository extends JpaRepository<WorkPlace, Long> {
    Optional<WorkPlace> findById(Long id);

    @Query("""
                SELECT w FROM WorkPlace w 
                WHERE (:districtId IS NULL OR w.district.id = :districtId)
                AND (:regionId IS NULL OR w.district.region.id = :regionId)
                AND (:medicalInstitutionType IS NULL OR w.medicalInstitutionType = :medicalInstitutionType)
                            AND w.status='ACTIVE'
                            ORDER BY w.district.id  DESC 
            """)
    List<WorkPlace> findByFilters(@Param("districtId") Long districtId,
                                  @Param("regionId") Long regionId,
                                  @Param("medicalInstitutionType") MedicalInstitutionType medicalInstitutionType);

    @Query("""
                SELECT COUNT(w) FROM WorkPlace w 
                WHERE w.district.region.id = :regionId
            """)
    Long countByRegionId(@Param("regionId") Long regionId);

    @Query("""
                SELECT COUNT(w) FROM WorkPlace w 
                WHERE w.district.id = :districtId
            """)
    Long countByDistrictId(@Param("districtId") Long districtId);

}