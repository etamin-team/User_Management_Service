package com.example.user_management_service.repository;

import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.ManagerGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ManagerGoalRepository extends JpaRepository<ManagerGoal, Long> {
    @Query("SELECT mg FROM ManagerGoal mg " +
            "JOIN FETCH mg.medicines m " +
            "JOIN FETCH mg.districts d " +
            "WHERE mg.status = :status")
    List<ManagerGoal> findByStatusWithMedicinesAndDistricts(@Param("status") GoalStatus status);
}