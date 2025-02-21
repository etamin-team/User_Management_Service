package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Date-2/21/2025
 * By Sardor Tokhirov
 * Time-2:29 PM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionsToPreparateDto {
    private Long id;
    private Integer minPercentage;
    private Integer minPercentageVal;
    private Integer maxPercentage;
    private Integer maxPercentageVal;
    private List<PercentageValDto> percentageVals;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer su;
    private Integer sb;
    private Integer gz;
    private Integer kb;
}