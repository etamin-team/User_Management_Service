package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-8/7/2025
 * By Sardor Tokhirov
 * Time-3:53 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPrescriptions {
    private Medicine medicine;
    private Long medicineId;
    private Long quote;
    private Long written;
    private Long kb;
    private Long su;
    private Long sb;
    private Long gz;
    private Long recipe;
}
