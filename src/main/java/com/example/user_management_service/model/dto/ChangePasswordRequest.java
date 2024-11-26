package com.example.user_management_service.model.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-3:07 AM (GMT+5)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    private String userId;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}