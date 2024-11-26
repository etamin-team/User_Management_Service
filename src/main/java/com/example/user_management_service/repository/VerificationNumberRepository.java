package com.example.user_management_service.repository;

import com.example.user_management_service.model.VerificationNumber;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Date-11/25/2024
 * By Sardor Tokhirov
 * Time-4:30 AM (GMT+5)
 */
@Transactional
@Repository
public interface VerificationNumberRepository extends JpaRepository<VerificationNumber, Long> {

    Optional<VerificationNumber> findByNumber(String number);


    @Modifying
    @Query("DELETE FROM VerificationNumber vn WHERE vn.number = :number")
    void deleteByUser(@Param("number") String number);

}