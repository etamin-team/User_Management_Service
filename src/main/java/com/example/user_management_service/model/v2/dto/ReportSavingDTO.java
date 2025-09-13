package com.example.user_management_service.model.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

/**
 * Date-9/13/2025
 * By Sardor Tokhirov
 * Time-7:01 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSavingDTO {
    private Long id;
    private YearMonth yearMonth;
    private boolean isSaved;
    private Long regionId;
}