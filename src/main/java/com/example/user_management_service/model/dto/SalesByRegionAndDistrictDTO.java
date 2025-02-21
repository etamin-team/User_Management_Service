package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesByRegionAndDistrictDTO {
    private Long id;
    private Long medicineId;
    private String group;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long allDirectSales;
    private Long allSecondarySales;
    private Long quote;
    private Long total;

    private RegionDistrictDTO regionDTO;
}
