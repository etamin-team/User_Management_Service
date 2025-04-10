package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-4/8/2025
 * By Sardor Tokhirov
 * Time-5:28 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminReportDTO {
    private Long medicineId;

    private Medicine medicine;

    private Long percentage;

    private Long allowed;

    private Long recipe;

    private Long su;

    private Long sb;

    private  Long gz;

    private  Long kb;
}
