//package com.example.user_management_service.repository;
//
//import com.example.user_management_service.model.MedicineAgentGoalQuantity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface MedicineAgentGoalQuantityRepository extends JpaRepository<MedicineAgentGoalQuantity, Long> {
//
//    @Query("SELECT mwq.contractMedicineMedAgentAmount FROM MedicineAgentGoalQuantity mwq WHERE mwq.medicine.id = :medicineId AND mwq.agentGoal.id = :goalId")
//    Optional<MedicineAgentGoalQuantity> findContractMedicineAmountByMedicineIdAndContractId(
//            @Param("medicineId") Long medicineId,
//            @Param("goalId") Long goalId);
//}
