package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-2/8/2025
 * By Sardor Tokhirov
 * Time-9:00 AM (GMT+5)
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OutOfContractAmountDTO {
    private UUID doctorId;
    private List<OutOfContractMedicineAmountDTO> outOfContractMedicineAmount;

}
