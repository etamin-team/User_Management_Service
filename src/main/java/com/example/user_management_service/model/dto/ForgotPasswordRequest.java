package com.example.user_management_service.model.dto;

import lombok.*;

/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-3:10 AM (GMT+5)
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    private String phoneNumber;
}
