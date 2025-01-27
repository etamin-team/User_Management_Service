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
    private Long salesId;
    private String medicineName;
    private Long cip;
    private LocalDate salesDate;
    private List<SalesByRegionDTO> salesByRegion;

}
