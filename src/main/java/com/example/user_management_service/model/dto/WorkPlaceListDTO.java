package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.MedicalInstitutionType;
import com.example.user_management_service.service.PasswordResetService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/18/2025
 * By Sardor Tokhirov
 * Time-7:03 PM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkPlaceListDTO {

    private Long id;

    private UserDTO userDTO;

    private RegionDistrictDTO regionDistrictDTO;

    private MedicalInstitutionType medicalInstitutionType;
    private String address;
    private String description;
    private String phone;
    private String email;
    private String name;
}
