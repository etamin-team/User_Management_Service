package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Date-4/14/2025
 * By Sardor Tokhirov
 * Time-8:30 AM (GMT+5)
 */
@Data
@AllArgsConstructor
public class LineChart {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalPrice;
}
