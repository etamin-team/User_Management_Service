package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Date-3/29/2025
 * By Sardor Tokhirov
 * Time-11:58 AM (GMT+5)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldForceRegionsInfoDTO {

    private Long id;
    private UserDTO userDTO;
    private List<RegionDTO> regionIds;
}
