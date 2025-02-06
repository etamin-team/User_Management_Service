package com.example.user_management_service.repository;

import com.example.user_management_service.model.DistrictGoalQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Date-2/6/2025
 * By Sardor Tokhirov
 * Time-5:34 AM (GMT+5)
 */

@Repository
public interface DistrictGoalQuantityRepository extends JpaRepository<DistrictGoalQuantity, Long> {
}
