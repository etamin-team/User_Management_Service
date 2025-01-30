package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesByRegionDTO {
        private Long districtId;
        private Integer directSales;
        private Integer secondarySales;
        private Long quote;
        private Integer totalSales;
        private LocalDate salesDate;
}
