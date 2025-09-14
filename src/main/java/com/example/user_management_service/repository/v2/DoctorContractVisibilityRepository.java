package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.DoctorContractVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DoctorContractVisibilityRepository extends JpaRepository<DoctorContractVisibility, Long> {
    DoctorContractVisibility findByDoctorId(UUID doctorId);
}