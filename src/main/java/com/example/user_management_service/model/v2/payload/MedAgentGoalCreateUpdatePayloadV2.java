package com.example.user_management_service.model.v2.payload;

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
 * Time-7:28 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedAgentGoalCreateUpdatePayloadV2 {
    private Long goalId;
    private UUID agentId;
    private GoalStatus goalStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MedicineQuotePayloadV2> medicineQuotes;
    private List<FieldEnvQuotePayloadV2> fieldEnvQuotes;
}