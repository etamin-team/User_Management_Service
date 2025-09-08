package com.example.user_management_service.model.v2.payload;

import com.example.user_management_service.model.GoalStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-2:51 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerGoalCreateUpdatePayloadV2 {

    private Long goalId;

    private GoalStatus goalStatus;

    private UUID managerId;

    List<MedicineQuotePayloadV2> medicineQuotes;

    List<FieldEnvQuotePayloadV2> fieldEnvQuotes;

    List<MedAgentQuoteEnvPayloadV2> medAgenEnvQuotes;

    private LocalDate startDate;

    private LocalDate endDate;

}
