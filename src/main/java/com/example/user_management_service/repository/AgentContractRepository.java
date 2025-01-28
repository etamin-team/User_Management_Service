package com.example.user_management_service.repository;

import com.example.user_management_service.model.AgentContract;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AgentContractRepository extends JpaRepository<AgentContract, Long> {
}
