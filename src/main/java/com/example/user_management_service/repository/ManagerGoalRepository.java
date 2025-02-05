package com.example.user_management_service.repository;

import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.ManagerGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ManagerGoalRepository extends JpaRepository<ManagerGoal, Long> {
    @Query("SELECT m FROM ManagerGoal m WHERE m.managerId.userId = :managerId "+
            "AND m.startDate <= CURRENT_DATE " +
            "AND m.endDate >= CURRENT_DATE")
    Optional<ManagerGoal> getGoalsByManagerId(@Param("managerId") UUID managerId);

}