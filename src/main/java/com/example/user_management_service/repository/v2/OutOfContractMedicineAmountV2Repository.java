package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.OutOfContractMedicineAmountV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-2:50 AM (GMT+5)
 */
@Repository
public interface OutOfContractMedicineAmountV2Repository extends JpaRepository<OutOfContractMedicineAmountV2, Long> {
    @Query("SELECT o FROM OutOfContractMedicineAmountV2 o WHERE o.doctor.userId = :doctorId AND o.yearMonth = :yearMonth")
    Optional<List<OutOfContractMedicineAmountV2>> findAllForDoctorThisMonth(UUID doctorId, YearMonth yearMonth);
}
