package com.example.user_management_service.repository;

import com.example.user_management_service.model.AgentContract;
import com.example.user_management_service.model.ContractMedicineAmount;
import com.example.user_management_service.model.ManagerGoal;
import com.example.user_management_service.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractMedicineAmountRepository extends JpaRepository<ContractMedicineAmount, Long> {


}