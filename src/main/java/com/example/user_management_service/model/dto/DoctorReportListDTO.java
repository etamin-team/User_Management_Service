package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.v2.dto.ContractDTOV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-3:29 PM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DoctorReportListDTO {
    private UserDTO doctor;
    private ContractDTOV2 contractDTO;
}
