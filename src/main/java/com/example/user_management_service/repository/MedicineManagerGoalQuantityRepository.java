package com.example.user_management_service.repository;

import com.example.user_management_service.model.MedicineManagerGoalQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineManagerGoalQuantityRepository extends JpaRepository<MedicineManagerGoalQuantity, Long> {

    @Query("SELECT mgq FROM MedicineManagerGoalQuantity mgq WHERE mgq.medicine.id = :medicineId AND mgq.managerGoal.goalId = :goalId")
    Optional<MedicineManagerGoalQuantity> findContractMedicineAmountByMedicineIdAndGoalId(
            @Param("medicineId") Long medicineId,
            @Param("goalId") Long goalId);
}
