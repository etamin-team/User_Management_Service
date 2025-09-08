package com.example.user_management_service.model.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Date-8/23/2025
 * By Sardor Tokhirov
 * Time-3:51 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerProfileKPIDTOV2 {
    private UUID managerId;

    private long doctorsInDatabase;
    private long totalPrescriptions;
    private long prescriptionsNewDoctors;

    private long totalWorking;
    private long totalMedicines;
    private long medicineNewDoctors;

    private long newDoctorsThisMonth;
    private long coveredDistricts;
    private long coveredWorkPlaces;
}
