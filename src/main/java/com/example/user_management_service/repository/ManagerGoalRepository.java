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

}