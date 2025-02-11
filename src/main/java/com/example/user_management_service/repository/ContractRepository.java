package com.example.user_management_service.repository;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:37 PM (GMT+5)
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query("SELECT c FROM Contract c WHERE c.status = :status")
    Page<Contract> findByStatus(@Param("status") GoalStatus status, Pageable pageable);


    @Query("SELECT c FROM Contract c " +
            "WHERE c.doctor.userId = :doctorId " +
            "AND c.startDate <= CURRENT_DATE " +
            "AND c.endDate >= CURRENT_DATE " +
            "AND c.status = 'APPROVED'")
    Optional<Contract> findActiveContractByDoctorId(@Param("doctorId") UUID doctorId);


    @Modifying
    @Query("UPDATE Contract c SET c.status = :status WHERE c.id = :id")
    void updateContractStatus(@Param("id") UUID id, @Param("status") GoalStatus status);

    @Query("SELECT c FROM Contract c " +
            "WHERE c.medAgent.userId = :agentId")
    Page<Contract> findAllContractsByAgent(@Param("agentId") UUID agentId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT c.doctor) FROM Contract c WHERE c.medAgent.userId = :medAgentId")
    Integer countDoctorsByMedAgent(@Param("medAgentId") UUID medAgentId);

    @Query("SELECT COUNT(DISTINCT c.doctor) FROM Contract c " +
            "WHERE c.medAgent.userId = :medAgentId " +
            "AND EXTRACT(YEAR FROM c.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM c.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)")
    Integer countDoctorsByMedAgentThisMonth(@Param("medAgentId") UUID medAgentId);



    @Query("SELECT COUNT(c) FROM Contract c WHERE c.medAgent.userId = :medAgentId")
    Integer countContractsByMedAgent(@Param("medAgentId") UUID medAgentId);

    @Query("SELECT COUNT(c) FROM Contract c " +
            "WHERE c.medAgent.userId = :medAgentId " +
            "AND EXTRACT(YEAR FROM c.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM c.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)")
    Integer countContractsCreatedThisMonthByMedAgent(@Param("medAgentId") UUID medAgentId);


}
