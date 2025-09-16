package com.example.user_management_service.controller;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.model.v2.dto.ContractDTOV2;
import com.example.user_management_service.service.*;
import com.example.user_management_service.service.v2.DoctorServiceV2;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-12/20/2024
 * By Sardor Tokhirov
 * Time-6:45 AM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PasswordResetService passwordResetService;
    private final RecipeService recipeService;
    private final ContractService contractService;
    private final DoctorServiceV2 doctorServiceV2;

    @GetMapping("/doctors/not-declined-not-enabled")
    public Page<UserDTO> getDoctorsNotDeclinedAndNotEnabled(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminService.getDoctorsNotDeclinedAndNotEnabled(pageable);
    }

    @PatchMapping("/{id}/user-enable")
    public ResponseEntity<String> enableUser(@PathVariable UUID id) {
        adminService.enableUser(id);
        return ResponseEntity.ok("User has been enabled successfully.");
    }

    @PatchMapping("/{id}/user-decline")
    public ResponseEntity<String> declineUser(@PathVariable UUID id) {
        adminService.declineUser(id);
        return ResponseEntity.ok("User has been declined successfully.");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            boolean success = passwordResetService.resetPassword(request);
            if (success) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password reset successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting password");
        }
    }


    @GetMapping("/recipes")
    public ResponseEntity<List<LastRecipeDTO>> getAllLastRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Field category,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<LastRecipeDTO> recipes = recipeService.getRecipes(name, districtId, category, regionId, medicineId, startDate, endDate);
        return ResponseEntity.ok(recipes);
    }





}
