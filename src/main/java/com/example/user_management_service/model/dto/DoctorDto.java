package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Date-1/13/2025
 * By Sardor Tokhirov
 * Time-4:30 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String number;
    private String email;
    private String position;
    private Field fieldName;
    private Gender gender;

    private Long workplaceId;
    private Long districtId;

}
