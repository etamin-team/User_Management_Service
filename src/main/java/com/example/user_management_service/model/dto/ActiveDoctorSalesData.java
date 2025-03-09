package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;

/**
 * Date-3/9/2025
 * By Sardor Tokhirov
 * Time-6:57 AM (GMT+5)
 */

public class ActiveDoctorSalesData {
    private Long amount;
    private Month month;
    public ActiveDoctorSalesData(Long amount, Integer month) {
        this.month = (month != null) ? Month.of(month) : null;
    }

    // Getters
    public Long getAmount() { return amount; }
    public Month getMonth() { return month; }
}
