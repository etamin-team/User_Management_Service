package com.example.user_management_service.controller;

import com.example.user_management_service.model.User;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.model.dto.ResetPasswordRequest;
import com.example.user_management_service.model.dto.WorkPlaceDTO;
import com.example.user_management_service.service.AdminService;
import com.example.user_management_service.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Date-12/20/2024
 * By Sardor Tokhirov
 * Time-6:45 AM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public AdminController(AdminService adminService, PasswordResetService passwordResetService) {
        this.adminService = adminService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/doctors/not-declined-not-enabled")
    public Page<User> getDoctorsNotDeclinedAndNotEnabled(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminService.getDoctorsNotDeclinedAndNotEnabled(pageable);
    }
    @PatchMapping("/{id}/enable")
    public ResponseEntity<String> enableUser(@PathVariable UUID id) {
        adminService.enableUser(id);
        return ResponseEntity.ok("User has been enabled successfully.");
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<String> declineUser(@PathVariable UUID id) {
        adminService.declineUser(id);
        return ResponseEntity.ok("User has been declined successfully.");
    }

    @PutMapping("/workplaces/create")
    public ResponseEntity<String> createWorkPlace(@RequestBody WorkPlaceDTO workPlaceDTO) {
        adminService.createWorkPlace(workPlaceDTO);
        return new ResponseEntity<>("Workplace created successfully!", HttpStatus.CREATED);
    }

    @PutMapping("/workplaces/{id}")
    public ResponseEntity<String> updateWorkPlace(@PathVariable Long id, @RequestBody WorkPlaceDTO workPlaceDTO) {
        adminService.updateWorkPlace(id, workPlaceDTO);
        return ResponseEntity.ok("Workplace updated successfully!");
    }
    @DeleteMapping("/workplaces/{id}")
    public ResponseEntity<String> deleteWorkPlace(@PathVariable Long id) {
        adminService.deleteWorkPlace(id);
        return ResponseEntity.ok("Workplace deleted successfully!");
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
}
