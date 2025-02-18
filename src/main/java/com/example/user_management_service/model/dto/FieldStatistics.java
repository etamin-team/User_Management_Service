package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/18/2025
 * By Sardor Tokhirov
 * Time-8:55 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class FieldStatistics {
    private int allDoctors;
    private int doctorsInFact;
    private int writtenRecipes;
    public void incrementAllDoctors() {
        this.allDoctors++;
    }
}