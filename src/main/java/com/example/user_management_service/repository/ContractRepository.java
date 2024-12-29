package com.example.user_management_service.repository;

import com.example.user_management_service.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:37 PM (GMT+5)
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
}
