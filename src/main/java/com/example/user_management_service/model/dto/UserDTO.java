package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Date-1/11/2025
 * By Sardor Tokhirov
 * Time-3:22 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String number;
    private String email;
    private String position;
    private String fieldName;
    private Gender gender;

    private Long districtId;
    private Long workplaceId;
}
