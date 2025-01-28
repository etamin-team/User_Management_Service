package com.example.user_management_service.repository;

import com.example.user_management_service.model.AgentContract;
import com.example.user_management_service.model.MedicineWithQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineWithQuantityRepository extends JpaRepository<MedicineWithQuantity, Long> {
    List<MedicineWithQuantity> findByAgentContract(AgentContract agentContract);
}
