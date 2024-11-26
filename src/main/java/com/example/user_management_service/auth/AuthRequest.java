package com.example.user_management_service.auth;

import lombok.*;

/**
 * Date-11/20/2024
 * By Sardor Tokhirov
 * Time-4:53 PM (GMT+5)
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String number;
    private String password;
}
