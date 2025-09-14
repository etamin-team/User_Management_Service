package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private UUID notificationId;
    private UUID userId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdDate;
}