package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Date-2/25/2025
 * By Sardor Tokhirov
 * Time-6:12 AM (GMT+5)
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportListDTO {
    private LocalDate date;
    private List<SalesReportDTO> salesReportDTOS;


}
