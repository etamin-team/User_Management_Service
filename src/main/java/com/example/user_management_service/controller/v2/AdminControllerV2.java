package com.example.user_management_service.controller.v2;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.dto.AdminPrescriptions;
import com.example.user_management_service.model.dto.AdminPrescriptionsMedicine;
import com.example.user_management_service.model.dto.SimpleDoctorPrescriptionDTO;
import com.example.user_management_service.service.v2.AdminServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Date-8/7/2025
 * By Sardor Tokhirov
 * Time-3:24 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v2/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AdminControllerV2 {

    private final AdminServiceV2 adminServiceV2;

    @GetMapping("/prescriptions")
    public ResponseEntity<List<AdminPrescriptions>> getAdminPrescriptions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long workPlaceId,
            @RequestParam(required = false) Field field,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<AdminPrescriptions> adminPrescriptions = adminServiceV2.getAdminPrescriptions(name, regionId, districtId,workPlaceId,  field,  startDate, endDate);
        return ResponseEntity.ok(adminPrescriptions);
    }

    @GetMapping("/prescriptions/{medicineId}")
    public ResponseEntity<Page<AdminPrescriptionsMedicine>> getAdminPrescriptionsByMedicineId(
            @PathVariable Long medicineId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long workPlaceId,
            @RequestParam(required = false) Field field,
            @RequestParam(required = false) ContractType contractType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminPrescriptionsMedicine> adminPrescriptions = adminServiceV2.getAdminPrescriptionsByMedicineId(
                medicineId, regionId, districtId, workPlaceId, field, contractType, startDate, endDate, pageable);
        return ResponseEntity.ok(adminPrescriptions);
    }
}
