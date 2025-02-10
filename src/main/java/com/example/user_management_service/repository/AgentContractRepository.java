package com.example.user_management_service.repository;

import com.example.user_management_service.model.AgentContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AgentContractRepository extends JpaRepository<AgentContract, Long> {
    @Query("SELECT ac FROM AgentContract ac WHERE ac.medAgent.userId = :medAgentId "+
            "AND ac.startDate <= CURRENT_DATE " +
            "AND ac.endDate >= CURRENT_DATE")
    Optional<AgentContract> getContractsByMedAgentUserId(@Param("medAgentId") UUID medAgentId);
}
