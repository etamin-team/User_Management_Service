package com.example.user_management_service.model.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-7:44 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedAgentProfileKPIDTOV2 {
    private UUID medAgentId;

    long totalConnectedDoctors;
    long totalConnectedContracts;
    long connectedDoctorsCurrentMonth;

    long connectedContractsCurrentMonth;
    long prescriptionsIssuedCurrentMonth;
    long medicationsPrescribedCurrentMonth;

}
