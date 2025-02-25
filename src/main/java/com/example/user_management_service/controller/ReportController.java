package com.example.user_management_service.controller;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.MedicalInstitutionType;
import com.example.user_management_service.model.dto.DoctorReportDTO;
import com.example.user_management_service.model.dto.WorkPlaceListDTO;
import com.example.user_management_service.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-3:23 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/report")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{meidcineId}")
    public ResponseEntity<DoctorReportDTO> getDoctorReports(
            @PathVariable Long meidcineId,
            @RequestParam(required = false,defaultValue = "") String query,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) Field fieldName) {
        DoctorReportDTO doctorReportDTO = reportService.getDoctorReports(meidcineId,query, districtId, workplaceId, fieldName);
        return ResponseEntity.ok(doctorReportDTO);
    }
}
