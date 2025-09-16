package com.example.user_management_service.controller; // Note: This controller is still in V1 package.

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.MedicalInstitutionType;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.model.v2.dto.ContractDTOV2; // Corrected import
import com.example.user_management_service.service.ContractService;
import com.example.user_management_service.service.DataBaseService;
import com.example.user_management_service.service.RecipeService;
import com.example.user_management_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-5/31/2025
 * By Sardor Tokhirov
 * Time-3:59 AM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/field-force") // Path still indicates V1, but methods are V2
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class FieldForceController {


    private final UserService userService;
    private final DataBaseService dataBaseService;
    private final ContractService contractService;
    private final RecipeService recipeService;

    @GetMapping("/workplaces")
    public ResponseEntity<List<WorkPlaceListDTO>> getAllWorkPlaces(@RequestParam(required = false) List<Long> regionIds,
                                                                   @RequestParam(required = false) Long districtId,
                                                                   @RequestParam(required = false) Long regionId,
                                                                   @RequestParam(required = false) MedicalInstitutionType medicalInstitutionType) {
        List<WorkPlaceListDTO> workPlaceList = dataBaseService.getWorkPlacesByIds(regionIds,regionId,districtId,medicalInstitutionType);
        return ResponseEntity.ok(workPlaceList);
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<UserDTO>> getDoctors(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) Field field,
            @RequestParam(required = false) String nameQuery,
            @RequestParam(required = false, defaultValue = "false") boolean withContracts
    ) {
        List<UserDTO> doctors = userService.getDoctors(creatorId,regionId, regionIds, districtId, workplaceId, nameQuery, field, withContracts);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/doctors-page")
    public ResponseEntity<Page<UserDTO>> getDoctorsPage(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery,
            @RequestParam(required = false) Field field,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "false") boolean withContracts,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserDTO> doctors = userService.getDoctorsPage(regionIds,creatorId, regionId, districtId, workplaceId, nameQuery, field, medicineId, withContracts, startDate, endDate, page, size);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getManagers(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery) {
        List<UserDTO> users = userService.getManagers(regionIds,creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/medagents")
    public ResponseEntity<List<UserDTO>> getMedAgents(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery) {
        List<UserDTO> users = userService.getMedAgents(regionIds,creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/contracts") // Now returns Page<ContractDTOV2>
    public ResponseEntity<Page<ContractDTOV2>> getAllContracts(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workPlaceId,
            @RequestParam(required = false) String nameQuery,
            @RequestParam(required = false) Field fieldName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth // Add YearMonth
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());

        Page<ContractDTOV2> contracts;
        if (regionIds != null && !regionIds.isEmpty()) {
            contracts = contractService.getFilteredContractsV2(regionIds,
                    regionId, districtId, workPlaceId, nameQuery, fieldName, startDate, endDate, medicineId, page, size, targetMonth);
        } else {
            contracts = contractService.getFilteredContractsV2(regionId,
                    districtId, workPlaceId, nameQuery, fieldName, startDate, endDate, medicineId, page, size, targetMonth);
        }
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/recipes")
    public ResponseEntity<Page<RecipeDto>> filterRecipes(
            @RequestParam(required = false) String nameQuery,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) Field doctorField,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastAnalysisFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastAnalysisTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<RecipeDto> recipes = recipeService.filterRecipes(regionIds,
                nameQuery, regionId, districtId, medicineId, doctorField,
                lastAnalysisFrom, lastAnalysisTo, doctorId, page, size
        );
        return ResponseEntity.ok(recipes);
    }


    @GetMapping("/medagents-page")
    public ResponseEntity<Page<UserDTO>> getMedAgentsPage(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> users = userService.getMedAgentsFieldForcePage(regionIds,creatorId, regionId, districtId, workplaceId, nameQuery,page,size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/doctor/contracts/status")
    public ResponseEntity<Page<ContractDTOV2>> getContractsByStatus(
            @RequestParam GoalStatus goalStatus,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        Page<ContractDTOV2> contracts = contractService.getContractsByStatus(regionIds,goalStatus,targetMonth, page, size);
        return ResponseEntity.ok(contracts);
    }

}