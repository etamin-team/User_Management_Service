package com.example.user_management_service.repository;

import com.example.user_management_service.model.MNN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Date-3/8/2025
 * By Sardor Tokhirov
 * Time-8:25 AM (GMT+5)
 */
@Repository
public interface MNNRepository  extends JpaRepository<MNN, Long> {
}
