package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-9/7/2025
 * By Sardor Tokhirov
 * Time-9:56 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDTOV2 {
    private Long id;
    private UUID creatorId;
    private UUID doctorId;
    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private GoalStatus status;
    private ContractType contractType;

    private List<MedicineQuoteDTOV2> medicineQuoteDTOV2List;

}
