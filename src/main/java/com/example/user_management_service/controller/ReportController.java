package com.example.user_management_service.controller;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.MedicalInstitutionType;
import com.example.user_management_service.model.dto.DoctorReportDTO;
import com.example.user_management_service.model.dto.SalesReportDTO;
import com.example.user_management_service.model.dto.SalesReportListDTO;
import com.example.user_management_service.model.dto.WorkPlaceListDTO;
import com.example.user_management_service.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/manager/{medicineId}")
    public ResponseEntity<DoctorReportDTO> getDoctorReports(
            @PathVariable Long medicineId,
            @RequestParam(required = false,defaultValue = "") String query,
            @RequestParam ContractType contractType,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Field fieldName) {
        DoctorReportDTO doctorReportDTO = reportService.getDoctorReports(medicineId,contractType,query,regionId, districtId, workplaceId,startDate,endDate, fieldName);
        return ResponseEntity.ok(doctorReportDTO);
    }

    @GetMapping("/manager/report-list")
    public ResponseEntity<List<SalesReportDTO>> getDoctorReportsList(
            @RequestParam(required = false,defaultValue = "") String query,
            @RequestParam ContractType contractType,
            @RequestParam Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Field fieldName) {
        List<SalesReportDTO> salesReportDTOS = reportService.getSalesReportDTOList(contractType,query,regionId, districtId, workplaceId,startDate,endDate, fieldName);
        return ResponseEntity.ok(salesReportDTOS);
    }



    @GetMapping("/admin/{medicineId}")
    public ResponseEntity<SalesReportDTO> getSalesReports(
            @PathVariable Long medicineId,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Field fieldName) {

        SalesReportDTO salesReport= reportService.getSalesReportsByFilters(medicineId, query,regionId, districtId, workplaceId, fieldName,startDate,endDate);
        return ResponseEntity.ok(salesReport);
    }

    @GetMapping("/field-force/{medicineId}")
    public ResponseEntity<SalesReportDTO> getFieldForceSalesReports(
            @PathVariable Long medicineId,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Field fieldName) {

        SalesReportDTO salesReport= reportService.getFieldForceSalesReportsByFilters(regionIds,medicineId, query,regionId, districtId, workplaceId, fieldName,startDate,endDate);
        return ResponseEntity.ok(salesReport);
    }


    @PostMapping("/manager/save")
    public ResponseEntity<String> saveSalesReports(@RequestBody SalesReportListDTO salesReportListDTO) {
        reportService.saveSalesReports(salesReportListDTO);
        return ResponseEntity.ok("Sales reports saved successfully");
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editSalesReport(@PathVariable Long id, @RequestBody SalesReportDTO salesReportDTO) {
        reportService.editSalesReport(id, salesReportDTO);
        return ResponseEntity.ok("Sales report updated successfully");
    }

    @PutMapping("/correction/{quantityId}")
    public ResponseEntity<String> editMedicineQuantity(@PathVariable Long quantityId,    @RequestParam Long correction) {
        reportService.editMedicineQuantity(quantityId, correction);
        return ResponseEntity.ok("MedicineQuantity correction  updated successfully");
    }

}
