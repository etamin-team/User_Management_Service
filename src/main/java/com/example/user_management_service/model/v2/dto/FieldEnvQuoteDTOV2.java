package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-7:41 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldEnvQuoteDTOV2 {
    private Long id;
    private Field field;
    private Long quote;
    private Long amount;
    private YearMonth yearMonth;
}