package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-8/23/2025
 * By Sardor Tokhirov
 * Time-4:49 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerGoalDTOV2 {
    private Long id;
    private UUID managerId;

    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private GoalStatus status;

    private List<MedicineQuoteDTOV2> medicineQuoteDTOV2List;
    private List<FieldEnvDTOV2> fieldEnvDTOV2List;
    private List<MedAgentEnvDTOV2>  medAgentEnvDTOV2List;
}
