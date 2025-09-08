package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-7:38 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedAgentGoalDTOV2 {
    private Long id;
    private UUID agentId;
    private GoalStatus status;
    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MedicineQuoteDTOV2> medicineQuoteDTOV2List;
    private List<FieldEnvQuoteDTOV2> fieldEnvQuotes;
}