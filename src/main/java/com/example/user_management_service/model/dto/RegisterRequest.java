package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Gender;
import com.example.user_management_service.role.Role;
import lombok.*;

import java.time.LocalDate;

/**
 * Date-11/20/2024
 * By Sardor Tokhirov
 * Time-4:51 PM (GMT+5)
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private Role role;
    private String password;
    private String region;
    private String country;
    private String phoneNumber;
    private String phonePrefix;
    private String number;
    private String creatorId;
    private Long workPlaceId;
    private LocalDate birthDate;
    private Gender gender;

    private String fieldName;
    private String position;
}
