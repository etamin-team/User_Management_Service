package com.example.user_management_service.repository;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.MNN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Date-3/8/2025
 * By Sardor Tokhirov
 * Time-8:25 AM (GMT+5)
 */
@Repository
public interface MNNRepository  extends JpaRepository<MNN, Long> {
    List<MNN> findAllByOrderByNameAsc();
    List<MNN> findAllByOrderById();

    @Query("SELECT MAX(id) FROM MNN")
    Optional<Long> findMaxId();

    @Query("SELECT m FROM MNN m " +
            "WHERE :query IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT(:query, '%'))")
    Page<MNN> findMnnByNameAndSearch(@Param("query") String query,
                                 Pageable pageable);


}
