package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.dto.SalesQuoteDTO;
import com.example.user_management_service.model.v2.MedAgentEnvV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:43 PM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerProfileDTOV2 {
    private UUID managerId;
    private ManagerGoalDTOV2 managerGoalDTOV2;
    private SalesQuoteDTO salesQuoteDTO;
    private ManagerProfileKPIDTOV2 managerProfileKPIDTOV2;
}
