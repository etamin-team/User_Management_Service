package com.example.user_management_service.repository;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:09 PM (GMT+5)
 */

@Repository
public interface MedicineRepository  extends JpaRepository<Medicine, Long> {
}
