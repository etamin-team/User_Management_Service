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
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Field category,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        String[] nameParts = name != null ? name.split(" ") : new String[0];
        String firstName = nameParts.length > 0 ? nameParts[0] : null;
        String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : null;
        String middleName = nameParts.length > 2 ? nameParts[1] : null;


        List<LastRecipeDTO> recipes = recipeService.getRecipes(firstName, lastName, middleName, district, category, specialty, medicineId, startDate, endDate);
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
        return adminService.updateManagerGoal(id, updateGoalDTO)
                .map(updatedGoal -> new ResponseEntity<>(updatedGoal, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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


    // Med Agent Contract

    @PostMapping("/med-agent/new-contract")
    public ResponseEntity<AgentContractDTO> createAgentContract(@RequestBody AgentContractDTO agentContractDTO) {
        AgentContractDTO createdContract = adminService.createAgentContract(agentContractDTO);
        return ResponseEntity.ok(createdContract);
    }

    // Update an existing Agent Contract
    @PutMapping("/med-agent/contract/{contractId}")
    public ResponseEntity<AgentContractDTO> updateAgentContract(@PathVariable Long contractId,
                                                                @RequestBody   AgentContractDTO agentContractDTO) {
        AgentContractDTO updatedContract = adminService.updateAgentContract(contractId, agentContractDTO);
        return ResponseEntity.ok(updatedContract);
    }

    // Delete an Agent Contract
    @DeleteMapping("/med-agent/contract/{contractId}")
    public ResponseEntity<Void> deleteAgentContract(@PathVariable Long contractId) {
        adminService.deleteAgentContract(contractId);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/med-agent/contract/contract-id/{agentContractId}")
    public ResponseEntity<AgentContractDTO> getAgentContractById(@PathVariable Long agentContractId) {
        AgentContractDTO agentContractDTO = adminService.getAgentContractById(agentContractId);
        return ResponseEntity.ok(agentContractDTO);
    }

    @GetMapping("/med-agent/contract/agent-id/{medAgentId}")
    public ResponseEntity<AgentContractDTO> getAgentContractByMedAgentId(@PathVariable UUID medAgentId) {
        AgentContractDTO agentContractDTO = adminService.getAgentContractByMedAgentId(medAgentId);
        return ResponseEntity.ok(agentContractDTO);
    }

    // Doctor Contract


    // Create a new Contract
    @PostMapping("/doctor/new-contract")
    public ResponseEntity<ContractDTO> createContract(@RequestBody ContractDTO contractDTO) {
        ContractDTO createdContract = adminService.createContract(contractDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
    }

    // Update an existing Contract
    @PutMapping("/doctor/update-contract/{contractId}")
    public ResponseEntity<ContractDTO> updateContract(@PathVariable Long contractId, @RequestBody ContractDTO contractDTO) {
        ContractDTO updatedContract = adminService.updateContract(contractId, contractDTO);
        return ResponseEntity.ok(updatedContract);
    }

    // Delete a Contract
    @DeleteMapping("/doctor/delete-contract/{contractId}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long contractId) {
        adminService.deleteContract(contractId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/contracts/pending-review")
    public ResponseEntity<Page<ContractDTO>> getPendingReviewContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ContractDTO> contracts = contractService.getPendingReviewContracts(page, size);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/doctor/contract/{contractId}")
    public ResponseEntity<ContractAmountDTO> getContractById(@PathVariable Long contractId) {
        ContractAmountDTO contractAmountDTO = contractService.getContractById(contractId);
        return ResponseEntity.ok(contractAmountDTO);
    }
    @GetMapping("/doctor/contract/{doctorId}")
    public ResponseEntity<ContractAmountDTO> getContractByDoctorId(@PathVariable UUID doctorId) {
        ContractAmountDTO contractAmountDTO = contractService.getContractByDoctorId(doctorId);
        return ResponseEntity.ok(contractAmountDTO);
    }



    @PatchMapping("/contract/{id}/user-enable")
    public ResponseEntity<String> enableContract(@PathVariable Long id) {
        contractService.enableContract(id);
        return ResponseEntity.ok("Contract has been enabled successfully.");
    }

    @PatchMapping("/contract/{id}/user-decline")
    public ResponseEntity<String> declineContract(@PathVariable Long id) {
        contractService.declineContract(id);
        return ResponseEntity.ok("Contract  has been declined successfully.");
    }

}
