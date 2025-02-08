package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Date-2/7/2025
 * By Sardor Tokhirov
 * Time-9:50 AM (GMT+5)
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRecipeStatsDTO {
    private UUID doctorId;
    private Integer recipesCreatedThisMonth;
    private Double averageRecipesPerMonth;
}