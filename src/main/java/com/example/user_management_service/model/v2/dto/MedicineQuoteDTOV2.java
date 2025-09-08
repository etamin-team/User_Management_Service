package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.Medicine;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:45 PM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineQuoteDTOV2 {
    private Long id;
    private Medicine medicine;
    private Long quote;
    private Long amount;
    private YearMonth yearMonth;
}
