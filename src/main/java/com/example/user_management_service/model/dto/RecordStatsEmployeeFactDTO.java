package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-11:57 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordStatsEmployeeFactDTO {
    private String id;
    private String name;
    private long  lpuAmount;
    private long doctorsByDB;
    private long doctorsInFact;
    private long population;
}
