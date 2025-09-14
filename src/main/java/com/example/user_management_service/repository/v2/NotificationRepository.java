package com.example.user_management_service.repository.v2;

import com.example.user_management_service.model.v2.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Date-9/14/2025
 * By Sardor Tokhirov
 * Time-8:29 PM (GMT+5)
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserUserId(UUID userId);
    List<Notification> findByUserUserIdAndIsReadFalse(UUID userId);
    long countByUserUserIdAndIsReadFalse(UUID userId);
}