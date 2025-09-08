package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.ManagerGoalV2;
import com.example.user_management_service.model.v2.MedicineQuoteV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-7:02 PM (GMT+5)
 */
@Repository
public interface MedicineQuoteV2Repository extends JpaRepository<MedicineQuoteV2, Long> {
}
