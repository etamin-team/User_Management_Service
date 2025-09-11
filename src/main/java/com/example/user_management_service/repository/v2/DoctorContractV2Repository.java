package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.DoctorContractV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-9/11/2025
 * By Sardor Tokhirov
 * Time-2:28 AM (GMT+5)
 */
@Repository
public interface DoctorContractV2Repository extends JpaRepository<DoctorContractV2, Long> {
    @Query("SELECT d FROM DoctorContractV2 d WHERE d.doctor.userId = :doctorId " +
            "AND d.status = 'PENDING_REVIEW' " +
            "AND (d.endDate IS NULL OR d.endDate >= :currentDate)")
    Optional<DoctorContractV2> getContractsByDoctorId(@Param("doctorId") UUID doctorId,
                                                      @Param("currentDate") LocalDate currentDate);

    @Query("SELECT d FROM DoctorContractV2 d WHERE d.doctor.userId = :doctorId " +
            "AND d.status = 'APPROVED'")
    Optional<DoctorContractV2> findByDoctorUserId(@Param("doctorId") UUID doctorId);


    @Query("SELECT c FROM DoctorContractV2 c WHERE c.doctor.district.region.id = :regionId")
    List<DoctorContractV2> findByRegion(@Param("regionId") Long regionId);

    @Query("SELECT c FROM DoctorContractV2 c WHERE c.createdBy.userId = :agentId AND c.doctor.district.id = :districtId")
    List<DoctorContractV2> findByCreatedByAndDistrict(@Param("agentId") UUID agentId, @Param("districtId") Long districtId);



    @Query("SELECT COUNT(c) FROM DoctorContractV2 c WHERE c.createdBy.userId = :createdBy AND c.doctor.district.id = :districtId")
    Long countByCreatedByAndDistrict(@Param("createdBy") UUID createdBy, @Param("districtId") Long districtId);

    @Query("SELECT COUNT(c) FROM DoctorContractV2 c WHERE c.createdBy.userId = :createdBy AND c.doctor.district.id = :districtId " +
            "AND c.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedByAndDistrictAndCreatedBetween(@Param("createdBy") UUID createdBy, @Param("districtId") Long districtId,
                                                      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



    @Query("SELECT dcv FROM DoctorContractV2 dcv WHERE dcv.createdAt BETWEEN :startDate AND :endDate")
    List<DoctorContractV2> findByCreatedThisMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}