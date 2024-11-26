package com.example.user_management_service.auth;

import com.example.user_management_service.role.Role;
import lombok.*;

/**
 * Date-11/25/2024
 * By Sardor Tokhirov
 * Time-3:40 AM (GMT+5)
 */

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSignUpRequest {
    private String firstName;
    private String phoneNumber;
    private String phonePrefix;
    private String number;
    private String lastName;
    private String password;
    private String region;
    private String country;
    private Integer verificationNumber;
}
