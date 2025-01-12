package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-1/11/2025
 * By Sardor Tokhirov
 * Time-5:08 AM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistrictDTO {
    private Long districtId;
    private String name;
    private String nameUzCyrillic;
    private String nameUzLatin;
    private String nameRussian;
    private Long regionId;
}