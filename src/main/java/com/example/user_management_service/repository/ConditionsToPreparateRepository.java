package com.example.user_management_service.repository;

import com.example.user_management_service.model.ConditionsToPreparate;
import com.example.user_management_service.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Date-2/21/2025
 * By Sardor Tokhirov
 * Time-2:27 PM (GMT+5)
 */
@Repository
public interface ConditionsToPreparateRepository extends JpaRepository<ConditionsToPreparate, Long> {
}
