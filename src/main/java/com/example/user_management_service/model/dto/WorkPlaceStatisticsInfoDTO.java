package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Date-2/18/2025
 * By Sardor Tokhirov
 * Time-7:39 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkPlaceStatisticsInfoDTO {
    List<FieldStatistics> fieldList;
    private int allDoctors;
    private int doctorsInFact;
    private int writtenRecipes;
}


