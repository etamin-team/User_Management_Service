package com.example.user_management_service.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesDTO {

    private Long medicineId;
    private Long cip;
    private String group;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long allDirectSales;
    private Long allSecondarySales;
    private List<SalesRegionDTO> sales;

}
