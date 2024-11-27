package com.example.user_management_service.repository;

import com.example.user_management_service.model.WorkPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-12:17 PM (GMT+5)
 */
public interface WorkPlaceRepository extends JpaRepository<WorkPlace, Long> {
    Optional<WorkPlace> findById(Long id);

}