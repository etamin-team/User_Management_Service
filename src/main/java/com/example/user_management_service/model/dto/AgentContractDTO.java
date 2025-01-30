package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.FieldWithQuantity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO to represent an AgentContract.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AgentContractDTO {
    private Long id;
    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID medAgentId;
    private List<MedicineWithQuantityDTO> medicineWithQuantityDTOS;
    private List<FieldWithQuantityDTO> fieldWithQuantityDTOS;
    private Long managerGoalId;
    private Long districtId;
}