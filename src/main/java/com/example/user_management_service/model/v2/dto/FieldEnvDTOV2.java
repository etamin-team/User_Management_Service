package com.example.user_management_service.model.v2.dto;

import com.example.user_management_service.model.Field;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:49 PM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldEnvDTOV2 {
    private Long id;

    private Field field;

    private Long quote;

    private Long amount;

    private YearMonth yearMonth;

}
