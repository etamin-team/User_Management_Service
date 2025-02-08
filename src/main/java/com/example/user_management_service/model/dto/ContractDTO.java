package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:43 PM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContractDTO {

    private Long id;
    private UUID doctorId;
    private GoalStatus goalStatus;

    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long fieldId;
    private UUID agentId;
    private Long  agentContractId;

    private List<MedicineWithQuantityDTO> medicinesWithQuantities;

    private RegionDistrictDTO regionDistrictDTO;
}
