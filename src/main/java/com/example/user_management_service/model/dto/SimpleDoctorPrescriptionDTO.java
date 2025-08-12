package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Date-8/12/2025
 * By Sardor Tokhirov
 * Time-12:54 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleDoctorPrescriptionDTO {
    private Medicine medicine;
    private UUID userId;
    private Long written;
}