package com.example.user_management_service.repository;

import com.example.user_management_service.model.ContractFieldAmount;
import com.example.user_management_service.model.ContractMedicineAmount;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.FieldGoalQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Date-2/6/2025
 * By Sardor Tokhirov
 * Time-5:36 AM (GMT+5)
 */
@Repository
public interface FieldGoalQuantityRepository extends JpaRepository<FieldGoalQuantity, Long> {

    @Query("SELECT f.contractFieldAmount FROM FieldGoalQuantity f WHERE f.field = :field AND f.managerGoal.goalId = :goalId")
    Optional<ContractFieldAmount> findContractFieldAmountByFieldAndGoalId(
            @Param("field") Field field,
            @Param("goalId") Long goalId);
}
