package com.example.user_management_service.controller;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.service.*;
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
    private final RecipeService recipeService;
    private final ContractService contractService;

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


    //manager goal

    @PostMapping("/manager/new-goal")
    public ResponseEntity<ManagerGoalDTO> createManagerGoal(@RequestBody ManagerGoalDTO managerGoalDTO) {
        ManagerGoalDTO savedGoal = adminService.createManagerGoal(managerGoalDTO);
        return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
    }

    @PutMapping("/manager/goal/{id}")
    public ResponseEntity<ManagerGoalDTO> updateManagerGoal(
            @PathVariable Long id,
            @RequestBody ManagerGoalDTO updateGoalDTO
    ) {
        ResponseEntity<ManagerGoalDTO> managerGoalDTOResponseEntity = adminService.updateManagerGoal(id, updateGoalDTO)
                .map(updatedGoal -> new ResponseEntity<>(updatedGoal, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        return managerGoalDTOResponseEntity;
    }

    @DeleteMapping("/manager/goal/{id}")
    public ResponseEntity<Void> deleteManagerGoal(@PathVariable Long id) {
        if (adminService.deleteManagerGoal(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/manager/goal/goal-id/{goalId}")
    public ResponseEntity<ManagerGoalDTO> getManagerGoalById(@PathVariable Long goalId) {
        ManagerGoalDTO managerGoalDTO = adminService.getManagerGoalById(goalId);
        return ResponseEntity.ok(managerGoalDTO);
    }


    @GetMapping("/manager/goal/manager-id/{managerId}")
    public ResponseEntity<ManagerGoalDTO> getManagerGoalsByManagerId(@PathVariable UUID managerId) {
        ManagerGoalDTO managerGoals = adminService.getManagerGoalsByManagerId(managerId);
        return ResponseEntity.ok(managerGoals);
    }


    // Med Agent Goal

    @PostMapping("/med-agent/new-goal")
    public ResponseEntity<AgentContractDTO> createAgentGoal(@RequestBody AgentContractDTO agentContractDTO) {
        AgentContractDTO createdContract = adminService.createAgentGoal(agentContractDTO);
        return ResponseEntity.ok(createdContract);
    }

    @PutMapping("/med-agent/goal/{goalId}")
    public ResponseEntity<AgentContractDTO> updateAgentGoal(@PathVariable Long goalId,
                                                            @RequestBody AgentContractDTO agentContractDTO) {
        AgentContractDTO updatedContract = adminService.updateAgentGoal(goalId, agentContractDTO);
        return ResponseEntity.ok(updatedContract);
    }

    // Delete an Agent Goal
    @DeleteMapping("/med-agent/goal/{goalId}")
    public ResponseEntity<Void> deleteAgentGoal(@PathVariable Long goalId) {
        adminService.deleteAgentGoal(goalId);
        return ResponseEntity.noContent().build();
    }


    // Doctor Contract
    @GetMapping("/doctor/contracts/status")
    public ResponseEntity<Page<ContractDTO>> getContractsByStatus(
            @RequestParam GoalStatus goalStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ContractDTO> contracts = contractService.getContractsByStatus(goalStatus, page, size);
        return ResponseEntity.ok(contracts);
    }


    @PatchMapping("/contract/{id}/user-enable")
    public ResponseEntity<String> enableContract(@PathVariable Long id) {
        contractService.enableContract(id);
        return ResponseEntity.ok("Contract has been enabled successfully.");
    }
    @PatchMapping("/contract/{id}/edit-status")
    public ResponseEntity<String> editStatusContract(@PathVariable Long id,@RequestParam GoalStatus goalStatus) {
        contractService.editStatusContract(id,goalStatus);
        return ResponseEntity.ok("Contract has been enabled successfully.");
    }

    @PatchMapping("/contract/{id}/user-decline")
    public ResponseEntity<String> declineContract(@PathVariable Long id) {
        contractService.declineContract(id);
        return ResponseEntity.ok("Contract  has been declined successfully.");
    }


//    @GetMapping("/manager/kpi/{id}")
//    public ResponseEntity<ManagerKpiDTO> getManagerKpi(@PathVariable UUID id) {
//
//
//        return id;
//    }


}
