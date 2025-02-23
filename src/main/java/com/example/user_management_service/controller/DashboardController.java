package com.example.user_management_service.controller;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.dto.RecordDTO;
import com.example.user_management_service.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/filter")
    public ResponseEntity<List<RecordDTO>> getFilteredRecords(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) Field field,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<RecordDTO> records = dashboardService.getFilteredRecords(regionId, districtId, workplaceId, field, query, medicineId, startDate, endDate);
        return ResponseEntity.ok(records);
    }


}
