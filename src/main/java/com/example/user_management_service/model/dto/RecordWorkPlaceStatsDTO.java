package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-12:00 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordWorkPlaceStatsDTO {
    private Field field;
    private int doctorsByDB;
    private int doctorsInFact;
}
