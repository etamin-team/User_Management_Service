package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-1/13/2025
 * By Sardor Tokhirov
 * Time-10:46 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFullNameDTO {
    private String firstName;
    private String lastName;
    private String middleName;
}
