package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.Gender;
import lombok.*;

import java.time.LocalDate;

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
    private String lastName;
    private String middleName;

    private String phoneNumber;
    private String phonePrefix;
    private String number;

    private String password;

    private Long districtId;

    private LocalDate birthDate;

    private Gender gender;

    private Long workPlaceId;

    private Field fieldName;

    private String position;

//    private Boolean isNumber;
//    private String email;

//    private Integer verificationNumber;
}
