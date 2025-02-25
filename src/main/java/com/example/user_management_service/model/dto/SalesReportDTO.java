package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/25/2025
 * By Sardor Tokhirov
 * Time-6:12 AM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDTO {

    private Long id;
    private Long medicineId;
    private Long quantityId;
    private Long written ;
    private Long allowed ;
    private Long sold ;

    private MedicineWithQuantityDTO medicineWithQuantityDoctor;
}
