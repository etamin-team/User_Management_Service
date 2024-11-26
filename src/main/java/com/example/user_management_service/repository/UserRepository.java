package com.example.user_management_service.repository;

import com.example.user_management_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Date-11/19/2024
 * By Sardor Tokhirov
 * Time-5:10 PM (GMT+5)
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    Optional<User> findById(UUID uuid);

    Optional<User> findByNumber(String number);

    Optional<User> findByEmail(String email);
}