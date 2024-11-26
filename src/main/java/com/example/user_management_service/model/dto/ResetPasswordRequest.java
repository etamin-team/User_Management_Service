package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-3:09 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    private String resetToken;  // The token sent to the user
    private String newPassword; // The new password the user wants to set
}