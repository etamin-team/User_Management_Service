package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.ContractMedicineDoctorAmountV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-8/23/2025
 * By Sardor Tokhirov
 * Time-5:41 AM (GMT+5)
 */
@Repository
public interface ContractMedicineDoctorAmountV2Repository extends JpaRepository<ContractMedicineDoctorAmountV2, Long> {
    @Query("""
        SELECT SUM(cmda.amount)
        FROM ContractMedicineDoctorAmountV2 cmda
        JOIN cmda.medicineWithQuantityDoctor mwd
        JOIN mwd.medicine med
        JOIN mwd.doctorContract dc
        JOIN dc.doctor d
        JOIN d.district dist
        JOIN dist.region r
        WHERE r.id = :regionId
          AND med.id = :medicineId
          AND cmda.yearMonth = :yearMonth
    """)
    Optional<Long> sumMedicineAmountsByRegionMedicineAndMonth(
            @Param("regionId") Long regionId,
            @Param("medicineId") Long medicineId,
            @Param("yearMonth") YearMonth yearMonth
    );

    @Query("""
        SELECT SUM(cmda.amount)
        FROM ContractMedicineDoctorAmountV2 cmda
        JOIN cmda.medicineWithQuantityDoctor mwd
        JOIN mwd.medicine med
        JOIN mwd.doctorContract dc
        JOIN dc.createdBy agent 
        JOIN dc.doctor d 
        JOIN d.district dist 
        WHERE agent.userId = :agentId
          AND dist.id = :districtId
          AND med.id = :medicineId
          AND cmda.yearMonth = :yearMonth
    """)
    Optional<Long> sumMedicineAmountsByAgentDistrictMedicineAndMonth(
            @Param("agentId") UUID agentId,
            @Param("medicineId") Long medicineId,
            @Param("yearMonth") YearMonth yearMonth
    );
}
