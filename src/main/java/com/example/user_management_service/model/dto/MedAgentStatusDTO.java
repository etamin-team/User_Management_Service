package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Date-2/8/2025
 * By Sardor Tokhirov
 * Time-11:16 AM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedAgentStatusDTO {
    private UUID  id;

    private Integer allConnectedDoctors;

    private Integer connectedDoctorsThisMonth;

    private Integer allConnectedContracts;

    private Integer connectedContractsThisMonth;

    private Integer writtenRecipesThisMonth;

    private Integer writtenMedicinesThisMonth;
}
