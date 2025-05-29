package com.example.user_management_service.controller;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-2/22/2025
 * By Sardor Tokhirov
 * Time-6:51 PM (GMT+5)
 */

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class DashboardController {

    private DashboardService dashboardService;

    @GetMapping("/main-data")
    public ResponseEntity<RecordDTO> getMainRecords(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) Field field,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        RecordDTO records = dashboardService.getFilteredRecords(regionId, districtId, workplaceId, field, userId, medicineId, startDate, endDate);
        return ResponseEntity.ok(records);
    }
    @GetMapping("/sales-quote")
    public ResponseEntity<SalesQuoteDTO> getSales(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        SalesQuoteDTO salesQuoteDTO = dashboardService.getSalesQuote(regionId, startDate, endDate);
        return ResponseEntity.ok(salesQuoteDTO);
    }

    @GetMapping("/top-6-medicine")
    public ResponseEntity<List<TopProductsOnSellDTO>> getTop6MedicineRecords(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<TopProductsOnSellDTO> top6Medicine = dashboardService.getTop6Medicine(regionId, districtId, workplaceId, startDate, endDate);
        return ResponseEntity.ok(top6Medicine);
    }

    @GetMapping("/active-doctor-sales-data")
    public ResponseEntity<List<ActiveDoctorSalesData>> getActiveDoctorSalesData() {
        List<ActiveDoctorSalesData> activeDoctorSalesDataList = dashboardService.getActiveDoctorSalesData();
        return ResponseEntity.ok(activeDoctorSalesDataList);
    }

    @GetMapping("/contract-type-sales-data")
    public ResponseEntity< List<ContractTypeSalesData>> getContractTypeSalesData() {
        List<ContractTypeSalesData> contractTypeSalesData= dashboardService.getContractTypeSalesData();
        return ResponseEntity.ok(contractTypeSalesData);
    }
    @GetMapping("/dashboard-doctors-coverages")
    public ResponseEntity<List<DashboardDoctorsCoverage>> getDashboardDoctorsCoverages() {
        List<DashboardDoctorsCoverage>  dashboardDoctorsCoverages= dashboardService.getDashboardDoctorsCoverages();
        return ResponseEntity.ok(dashboardDoctorsCoverages);
    }


    @GetMapping("/chart/sales")
    public ResponseEntity<List<LineChart>> getSalesRecipeChart(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "36") int numberOfParts
    ) {
        List<LineChart> chart = dashboardService.getRecipeChartSales( startDate, endDate, numberOfParts);
        return ResponseEntity.ok(chart);
    }

    @GetMapping("/chart/quote")
    public ResponseEntity<List<LineChart>> getQuoteRecipeChart(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "36") int numberOfParts
    ) {
        List<LineChart> chart = dashboardService.getRecipeChartQuote( startDate, endDate, numberOfParts);
        return ResponseEntity.ok(chart);
    }

}
