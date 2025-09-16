//package com.example.user_management_service.repository;
//
//import com.example.user_management_service.model.DistrictGoalQuantity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
///**
// * Date-2/6/2025
// * By Sardor Tokhirov
// * Time-5:34 AM (GMT+5)
// */
//
//@Repository
//public interface DistrictGoalQuantityRepository extends JpaRepository<DistrictGoalQuantity, Long> {
//    @Query("SELECT d FROM DistrictGoalQuantity d WHERE d.managerGoal.goalId = :goalId AND d.district.id = :districtId")
//    Optional<DistrictGoalQuantity> findByGoalIdAndDistrictId(@Param("goalId") Long goalId, @Param("districtId") Long districtId);
//
//}
