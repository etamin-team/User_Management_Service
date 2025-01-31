package com.example.user_management_service.repository;

import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.ManagerGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface ManagerGoalRepository extends JpaRepository<ManagerGoal, Long> {
    @Query(value = "SELECT * FROM manager_goal WHERE manager_id.user_id = :managerId", nativeQuery = true)
    List<ManagerGoal> findByManagerUserId(@Param("managerId") UUID managerId);
}