package com.example.user_management_service.repository;

import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Date-11/21/2024
 * By Sardor Tokhirov
 * Time-3:18 PM (GMT+5)
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    @Query("SELECT r FROM Region r WHERE r.status = 'ACTIVE'")
    List<Region> findAllActive();
}
