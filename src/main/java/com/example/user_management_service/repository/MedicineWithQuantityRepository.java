package com.example.user_management_service.repository;

import com.example.user_management_service.model.ContractMedicineAmount;
import com.example.user_management_service.model.MedicineWithQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicineWithQuantityRepository extends JpaRepository<MedicineWithQuantity, Long> {

    @Query("SELECT mwq.contractMedicineAmount FROM MedicineWithQuantity mwq WHERE mwq.medicine.id = :medicineId AND mwq.agentGoal.id = :goalId")
    Optional<ContractMedicineAmount> findContractMedicineAmountByMedicineIdAndContractId(
            @Param("medicineId") Long medicineId,
            @Param("goalId") Long goalId);
}
