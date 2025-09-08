package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.dto.OutOfContractMedicineAmountDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-2:36 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorProfileDTOV2 {
    private UUID doctorId;
    private ContractDTOV2 contract;
    private List<OutOfContractMedicineAmountDTO> outOfContractMedicineAmount;

}
