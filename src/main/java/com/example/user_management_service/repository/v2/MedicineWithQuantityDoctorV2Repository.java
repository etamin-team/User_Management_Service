package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.MedicineWithQuantityDoctorV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Date-8/23/2025
 * By Sardor Tokhirov
 * Time-5:46 AM (GMT+5)
 */
@Repository
public interface MedicineWithQuantityDoctorV2Repository extends JpaRepository<MedicineWithQuantityDoctorV2, Long> {
}
