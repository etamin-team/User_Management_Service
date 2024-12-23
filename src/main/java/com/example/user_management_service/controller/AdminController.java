package com.example.user_management_service.controller;

import com.example.user_management_service.model.User;
import com.example.user_management_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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

}
