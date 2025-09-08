package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.District;
import com.example.user_management_service.model.dto.DistrictDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:50 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedAgentEnvDTOV2 {
    private Long id;
    private Long quote;
    private DistrictDTO district;
    private Long amount;
    private YearMonth yearMonth;
}
