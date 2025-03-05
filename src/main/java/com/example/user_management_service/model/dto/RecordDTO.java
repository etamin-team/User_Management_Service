package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Date-2/22/2025
 * By Sardor Tokhirov
 * Time-7:22 PM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDTO {

    private Long quote;
    private Long sales;


    private RecordRegionDTO recordRegionDTO;

    private RecordDistrictDTO recordDistrictDTO;

    private RecordWorkPlaceDTO recordWorkPlaceDTO;

    private List<TopProductsOnSellDTO> topProductsOnSellDTO;
}
