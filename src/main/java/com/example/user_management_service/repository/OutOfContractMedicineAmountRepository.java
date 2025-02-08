package com.example.user_management_service.repository;

import com.example.user_management_service.model.OutOfContractMedicineAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutOfContractMedicineAmountRepository extends JpaRepository<OutOfContractMedicineAmount, Long> {

    @Query("SELECT o FROM OutOfContractMedicineAmount o " +
            "WHERE o.doctor.userId = :doctorId " +
            "AND FUNCTION('MONTH', o.createdAt) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', o.createdAt) = FUNCTION('YEAR', CURRENT_DATE)")
    List<OutOfContractMedicineAmount> findAllForDoctorThisMonth(@Param("doctorId") UUID doctorId);
}
