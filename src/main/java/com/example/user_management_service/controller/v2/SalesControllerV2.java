package com.example.user_management_service.controller.v2;

import com.example.user_management_service.model.dto.SalesByRegionDTO;
import com.example.user_management_service.model.dto.SalesDTO;
import com.example.user_management_service.model.dto.SalesRegionDTO;
import com.example.user_management_service.service.v2.SalesServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Date-9/11/2025
 * By Sardor Tokhirov
 * Time-7:24 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v2/sales")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SalesControllerV2 {
    private final SalesServiceV2 salesService;
    @PostMapping("/load-data")
    public ResponseEntity<String> loadData(@RequestBody List<SalesDTO> salesDTOS) {
        try {
            salesService.saveSalesDTOList(salesDTOS);
            return ResponseEntity.ok("Sales data saved successfully.");
        } catch (ResponseStatusException e) {
            // Return the error message with the status code
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping("/{salesId}")
    public ResponseEntity<String> updateSales(@PathVariable Long salesId, @RequestBody SalesRegionDTO salesDTO) {
        try {
            salesService.updateSales(salesId, salesDTO);
            return ResponseEntity.ok("Sales data updated successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @DeleteMapping("/{salesId}")
    public ResponseEntity<String> deleteSales(@PathVariable Long salesId) {
        try {
            salesService.deleteSales(salesId);
            return ResponseEntity.ok("Sales data deleted successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/data")
    public ResponseEntity<Page<SalesByRegionDTO>> getSalesInfoByMedicine(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SalesByRegionDTO> salesInfo = salesService.getSalesData(yearMonth, page, size);
        return ResponseEntity.ok(salesInfo);
    }


}
