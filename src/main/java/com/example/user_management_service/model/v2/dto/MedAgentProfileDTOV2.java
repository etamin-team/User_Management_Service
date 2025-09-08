package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.dto.SalesQuoteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-7:48 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedAgentProfileDTOV2 {
    private UUID medAgentId;

    private MedAgentGoalDTOV2 medAgentGoalDTO;
    private SalesQuoteDTO salesQuoteDTO ;
    private MedAgentProfileKPIDTOV2 medAgentProfileKPIDTOV2;

}
