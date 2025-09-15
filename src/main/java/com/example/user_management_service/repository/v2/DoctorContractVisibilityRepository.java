package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.DoctorContractVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface DoctorContractVisibilityRepository extends JpaRepository<DoctorContractVisibility, Long> {
    DoctorContractVisibility findByDoctorId(UUID doctorId);
    @Query("SELECT COUNT(v) FROM DoctorContractVisibility v WHERE v.isContractVisible = true")
    long countVisibleContracts();

    // New query to count contracts explicitly set to not visible
    @Query("SELECT COUNT(v) FROM DoctorContractVisibility v WHERE v.isContractVisible = false")
    long countNotVisibleContracts();

    // New query to count ALL explicit visibility records
    @Query("SELECT COUNT(v) FROM DoctorContractVisibility v")
    long countAllVisibilityRecords();
}