package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.ContractType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecipeDto {
    private UUID doctorId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String phoneNumberPrefix;
    private LocalDate dateCreation;
    private String diagnosis;
    private String comment;
    private Long telegramId;
    private Long districtId;
    private ContractType contractType;
    private List<PreparationDto> preparations;

    private UserDTO doctor;
}