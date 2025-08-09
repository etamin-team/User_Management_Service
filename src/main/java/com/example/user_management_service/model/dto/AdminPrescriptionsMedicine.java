package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-8/9/2025
 * By Sardor Tokhirov
 * Time-6:22 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPrescriptionsMedicine {
    private Medicine medicine;
    private UserDTO userDTO;
    private RegionDTO regionDTO;
    private Long written;
}
