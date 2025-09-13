package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private Medicine medicine;
    private Long medicineId;
    private Long written ;
    private Long allowed ;
    private Long sold ;
    private boolean saved;
    private ContractType contractType;

    private List<DoctorReportListDTO> doctorReportListDTOS;
}
