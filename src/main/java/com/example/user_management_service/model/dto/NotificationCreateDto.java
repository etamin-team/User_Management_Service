package com.example.user_management_service.model.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
/**
 * Date-9/14/2025
 * By Sardor Tokhirov
 * Time-8:33 PM (GMT+5)
 */
@Getter
@Setter
public class NotificationCreateDto {
    private UUID userId;
    private String message;
}