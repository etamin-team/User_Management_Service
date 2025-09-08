package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.DoctorContractV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-8/23/2025
 * By Sardor Tokhirov
 * Time-5:42 AM (GMT+5)
 */
@Repository
public interface DoctorContractV2Repository extends JpaRepository<DoctorContractV2, Long> {
    @Query("SELECT d FROM DoctorContractV2 d WHERE d.doctor.userId = :doctorId " +
            "AND d.status = 'PENDING_REVIEW' " +
            "AND (d.endDate IS NULL OR d.endDate >= :currentDate)")
    Optional<DoctorContractV2> getContractsByDoctorId(@Param("doctorId") UUID doctorId,
                                                      @Param("currentDate") LocalDate currentDate);
    @Query("SELECT d FROM DoctorContractV2 d WHERE d.doctor.userId = :doctorId " +
            "AND d.status = 'APPROVED' " )
    Optional<DoctorContractV2> findByDoctorUserId(@Param("doctorId") UUID doctorId);

    @Query("SELECT c FROM DoctorContractV2 c WHERE YEAR(c.createdAt) = :#{#yearMonth.year} AND MONTH(c.createdAt) = :#{#yearMonth.month}")
    List<DoctorContractV2> findByCreatedThisMonth(YearMonth yearMonth);

    @Query("SELECT c FROM DoctorContractV2 c WHERE c.doctor.district.region.id = :regionId")
    List<DoctorContractV2> findByRegion(Long regionId);
}
