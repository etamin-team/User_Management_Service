package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-3:22 PM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DoctorReportDTO {
    private Medicine medicine;
    private Long allowed;
    private Long written;
    private Long writtenInFact;
    private List<DoctorReportListDTO> doctorReportListDTOList;
}
