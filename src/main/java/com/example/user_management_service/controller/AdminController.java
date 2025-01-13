package com.example.user_management_service.controller;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.service.AdminService;
import com.example.user_management_service.service.DataBaseService;
import com.example.user_management_service.service.PasswordResetService;
import com.example.user_management_service.service.RecipeService;
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
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PasswordResetService passwordResetService;
    private final DataBaseService dataBaseService;
    private final RecipeService recipeService;

    @GetMapping("/doctors/not-declined-not-enabled")
    public Page<UserDTO> getDoctorsNotDeclinedAndNotEnabled(
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

    @PostMapping("/workplaces/create")
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


    @PostMapping("/contracts")
    public ResponseEntity<Contract> createContract(@RequestBody ContractDTO contractDTO) {
        Contract savedContract = dataBaseService.saveContractFromDTO(contractDTO);
        return ResponseEntity.ok(savedContract);
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<LastRecipeDTO>> getAllLastRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // Split the 'name' parameter into first, last, and middle names
        String[] nameParts = name != null ? name.split(" ") : new String[0];
        String firstName = nameParts.length > 0 ? nameParts[0] : null;
        String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : null;
        String middleName = nameParts.length > 2 ? nameParts[1] : null;

        // Call the service to fetch filtered data
        List<LastRecipeDTO> recipes = recipeService.getRecipes(firstName, lastName, middleName, district, category, specialty, medicineId, startDate, endDate);
        return ResponseEntity.ok(recipes);
    }

}
