package com.example.user_management_service.model.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-11:53 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsEmployeeDTO {
    private Long id;
    private String name;
    private String nameUzCyrillic;
    private String nameUzLatin;
    private String nameRussian;
    private Long  amount;
}
