package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Date-3/28/2025
 * By Sardor Tokhirov
 * Time-3:22 AM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldForceRegionsDTO {

    private Long id;

    private UUID fieldForceId;

    private List<Long> forceRegionIds;
}
