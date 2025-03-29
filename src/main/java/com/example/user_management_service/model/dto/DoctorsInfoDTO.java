package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-3/29/2025
 * By Sardor Tokhirov
 * Time-4:53 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorsInfoDTO {
    private Long allDoctors;
    private Long doctorsInFact;
    private Long newDoctors;
}
