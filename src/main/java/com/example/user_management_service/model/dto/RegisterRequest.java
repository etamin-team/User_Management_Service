package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
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
    private String email;

    private Role role;

    private String password;

    private Long DistrictId;

    private String phoneNumber;
    private String phonePrefix;
    private String number;

    private Long workPlaceId;
    private LocalDate birthDate;
    private Gender gender;

    private Field fieldName;
    private String position;
    private String groupName;
}
