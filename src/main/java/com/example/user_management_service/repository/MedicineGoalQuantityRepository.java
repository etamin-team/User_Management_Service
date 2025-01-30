package com.example.user_management_service.repository;

import com.example.user_management_service.model.ContractMedicineAmount;
import com.example.user_management_service.model.MedicineGoalQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineGoalQuantityRepository extends JpaRepository<MedicineGoalQuantity, Long> {

    @Query("SELECT mgq.contractMedicineAmount FROM MedicineGoalQuantity mgq WHERE mgq.medicine.id = :medicineId AND mgq.managerGoal.goalId = :goalId")
    Optional<ContractMedicineAmount> findContractMedicineAmountByMedicineIdAndGoalId(
            @Param("medicineId") Long medicineId,
            @Param("goalId") Long goalId);
}
