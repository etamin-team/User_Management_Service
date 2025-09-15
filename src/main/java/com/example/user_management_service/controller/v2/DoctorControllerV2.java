package com.example.user_management_service.controller.v2;

import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.dto.RecipeDto;
import com.example.user_management_service.model.v2.dto.ContractDTOV2;
import com.example.user_management_service.model.v2.dto.DoctorProfileDTOV2;
import com.example.user_management_service.model.v2.dto.VisibilityRequest;
import com.example.user_management_service.model.v2.payload.DoctorContractCreateUpdatePayloadV2;
import com.example.user_management_service.service.v2.DoctorServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-9/5/2025
 * By Sardor Tokhirov
 * Time-3:37 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v2/doctor")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class DoctorControllerV2 {

    private final DoctorServiceV2 doctorService2;

    // --- Existing methods (no YearMonth filter needed for these actions) ---
    @PostMapping("/contract/manager/create")
    public ResponseEntity<Void> createContractByManager(@RequestBody DoctorContractCreateUpdatePayloadV2 payload) {
        doctorService2.createContractByManager(payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/contract/med-agent/create")
    public ResponseEntity<Void> createContractByMedAgent(@RequestBody DoctorContractCreateUpdatePayloadV2 payload) {
        doctorService2.createContractByMedAgent(payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PutMapping("/contract/{contractId}")
    public ResponseEntity<Void> updateContract(@PathVariable Long contractId, @RequestBody DoctorContractCreateUpdatePayloadV2 payload) {
        doctorService2.updateContract(contractId, payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/contract/{contractId}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long contractId) {
        boolean deleted = doctorService2.deleteContract(contractId);
        return deleted ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/save-recipe")
    public ResponseEntity<Boolean> saveRecipe(@RequestBody RecipeDto recipe) {
        doctorService2.saveRecipe(recipe);
        return ResponseEntity.ok(true);
    }

    // --- GET methods with Optional YearMonth filter ---

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ContractDTOV2> getContractById(
            @PathVariable Long contractId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        ContractDTOV2 contract = doctorService2.getContractById(contractId, targetMonth);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/contract/doctor-id/{doctorId}") // This endpoint respects visibility
    public ResponseEntity<ContractDTOV2> getContractByDoctorId(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        ContractDTOV2 contract = doctorService2.getContractByDoctorId(doctorId, targetMonth);
        return contract != null ? ResponseEntity.ok(contract) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/profile/{doctorId}") // This endpoint respects contract visibility
    public ResponseEntity<DoctorProfileDTOV2> getDoctorProfileByDoctorId(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        DoctorProfileDTOV2 doctorProfileDTO = doctorService2.getDoctorProfileByDoctorId(doctorId, targetMonth);
        return ResponseEntity.ok(doctorProfileDTO);
    }

    // --- New/Renamed endpoints for manager-level access (explicitly ignoring visibility) ---

    @GetMapping("/contract/doctor-id/{doctorId}/for-manager")
    public ResponseEntity<ContractDTOV2> getContractByDoctorIdForManager(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        // Uses the service method that explicitly ignores visibility for managers
        ContractDTOV2 contract = doctorService2.getContractByDoctorIdForManager(doctorId, targetMonth);
        return contract != null ? ResponseEntity.ok(contract) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/profile/{doctorId}/for-manager")
    public ResponseEntity<DoctorProfileDTOV2> getDoctorProfileByDoctorIdForManager(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        // Uses the service method that explicitly ignores contract visibility for managers
        DoctorProfileDTOV2 doctorProfileDTO = doctorService2.getDoctorProfileByDoctorIdForManager(doctorId, targetMonth);
        return ResponseEntity.ok(doctorProfileDTO);
    }

    // --- Contract Visibility Management Endpoints ---
    @PutMapping("/contract/{doctorId}/visibility")
    public ResponseEntity<Void> setContractVisibility(@PathVariable UUID doctorId, @RequestBody VisibilityRequest request) {
        doctorService2.setContractVisibility(doctorId, request.isVisible());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    @PutMapping("/contract/all/visibility")
    public ResponseEntity<Void> setAllContractsVisibility(@RequestBody VisibilityRequest request) {
        doctorService2.setAllContractsVisibility(request.isVisible());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // --- Contract Status Management Endpoints ---
    @GetMapping("/contracts/status/{goalStatus}")
    public ResponseEntity<Page<ContractDTOV2>> getContractsByStatus(
            @PathVariable GoalStatus goalStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        Page<ContractDTOV2> contracts = doctorService2.getContractsByStatus(goalStatus, page, size, targetMonth);
        return ResponseEntity.ok(contracts);
    }

    @PutMapping("/contracts/{id}/enable")
    public ResponseEntity<Void> enableContract(@PathVariable Long id) {
        doctorService2.enableContract(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PutMapping("/contracts/{id}/status")
    public ResponseEntity<Void> editStatusContract(@PathVariable Long id, @RequestParam GoalStatus goalStatus) {
        doctorService2.editStatusContract(id, goalStatus);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PutMapping("/contracts/{id}/decline")
    public ResponseEntity<Void> declineContract(@PathVariable Long id) {
        doctorService2.declineContract(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // Inside your DoctorControllerV2 class

    @GetMapping("/contract/doctor-id/{doctorId}/is-visible")
    public ResponseEntity<Boolean> isContractVisible(@PathVariable UUID doctorId) {
        boolean isVisible = doctorService2.getContractVisibility(doctorId);
        return ResponseEntity.ok(isVisible);
    }
    @GetMapping("/contract/visibility/majority")
    public ResponseEntity<Boolean> getMostDoctorsContractVisibility() {
        boolean majorityVisible = doctorService2.getMostDoctorsContractVisibility();
        return ResponseEntity.ok(majorityVisible);
    }
}