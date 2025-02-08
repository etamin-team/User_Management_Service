package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/8/2025
 * By Sardor Tokhirov
 * Time-12:48 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDistrictDTO {
    private Long regionId;
    private String regionName;
    private String regionNameUzCyrillic;
    private String regionNameUzLatin;
    private String regionNameRussian;

    private Long districtId;
    private String districtName;
    private String districtNameUzCyrillic;
    private String districtNameUzLatin;
    private String districtNameRussian;
}
