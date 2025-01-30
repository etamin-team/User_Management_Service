package com.example.user_management_service.repository;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

}
