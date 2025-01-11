package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Date-1/11/2025
 * By Sardor Tokhirov
 * Time-5:04 AM (GMT+5)
 */
@Data
@AllArgsConstructor
public class RegionDTO {
    private Long id;
    private String name;
    private List<String> districtNames;  // List of district names
}