package com.example.user_management_service.model.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-9/14/2025
 * By Sardor Tokhirov
 * Time-10:02 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisibilityRequest {
    private boolean isVisible;
}