package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesByRegionDTO {
    private Long id;
    private Long medicineId;
    private String group;
    private Long allDirectSales;
    private Long allSecondarySales;
    private Long quote;
    private Long total;
    private Medicine medicine;

    private RegionDTO regionDTO;
}
