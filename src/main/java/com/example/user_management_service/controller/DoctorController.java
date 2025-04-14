package com.example.user_management_service.controller;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.service.ContractService;
import com.example.user_management_service.service.DoctorService;
import com.example.user_management_service.service.RecipeService;
import com.example.user_management_service.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-5:20 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/doctor")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class DoctorController {

    private final ContractService contractService;
    private final DoctorService doctorService;
    private final RecipeService recipeService;
    private final RoleService roleService;


    @PostMapping("/create-template")
    public void createTemplate(@RequestBody TemplateDto templateDto) {
        templateDto.setDoctorId(roleService.getCurrentUserId());
        doctorService.createTemplate(templateDto);
    }

    @PutMapping("/update-template")
    public void updateTemplate(@RequestBody TemplateDto templateDto) {
        templateDto.setDoctorId(roleService.getCurrentUserId());
        doctorService.saveTemplate(templateDto);
    }

    @PostMapping("/save-template/{id}")
    public void saveTemplate(@PathVariable Long id, @RequestParam boolean save) {
        doctorService.saveTemplate(id, save);
    }

    @DeleteMapping("/delete-template/{id}")
    public void deleteTemplate(@PathVariable Long id) {
        doctorService.deleteTemplate(id);
    }

    @GetMapping("/templates")
    public List<TemplateDto> getTemplates(
            @RequestParam(required = false) Boolean saved,
            @RequestParam(required = false, defaultValue = "false") Boolean sortBy,
            @RequestParam(required = false) String searchText) {
        return doctorService.getTemplates(saved, sortBy, searchText, roleService.getCurrentUserId());
    }

    @GetMapping("/find-medicines-by-inn")
    public ResponseEntity<List<Medicine>> findMedicineByInn(@RequestParam(required = false) List<String> inn, @RequestParam boolean exact) {
        List<Medicine> medicines = doctorService.findMedicinesByInn(inn,exact);
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }


    @PostMapping("/save-recipe")
    public ResponseEntity<Boolean> saveRecipe(@RequestBody RecipeDto recipe) {
        recipeService.saveRecipe(recipe);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/contract/contract-id/{contractId}")
    public ResponseEntity<ContractAmountDTO> getContractById(@PathVariable Long contractId) {
        ContractAmountDTO contractAmountDTO = contractService.getContractById(contractId);
        return ResponseEntity.ok(contractAmountDTO);
    }

    @GetMapping("/contract/doctor-id/{doctorId}")
    public ResponseEntity<ContractAmountDTO> getContractByDoctorId(@PathVariable UUID doctorId) {
        ContractAmountDTO contractAmountDTO = contractService.getActiveContractByDoctorId(doctorId);
        return ResponseEntity.ok(contractAmountDTO);
    }

    @GetMapping("/out-contract/doctor-id/{doctorId}")
    public ResponseEntity<OutOfContractAmountDTO> getOutOfContractByDoctorId(@PathVariable UUID doctorId) {
        OutOfContractAmountDTO contractAmountDTO = contractService.getOutOfContractsByDoctorId(doctorId);
        return ResponseEntity.ok(contractAmountDTO);
    }

    @GetMapping("/statistics/{doctorId}")
    public ResponseEntity<DoctorRecipeStatsDTO> getDoctorRecipeStatsDTOByDoctorId(@PathVariable UUID doctorId) {
        DoctorRecipeStatsDTO contractAmountDTO = contractService.getDoctorRecipeStatsDTOByDoctorId(doctorId);
        return ResponseEntity.ok(contractAmountDTO);
    }

    @GetMapping("/chart/{doctorId}")
    public ResponseEntity<List<LineChart>> getDoctorRecipeChart(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "36") int numberOfParts
    ) {
        List<LineChart> chart = contractService.getDoctorRecipeChart(doctorId, startDate, endDate, numberOfParts);
        return ResponseEntity.ok(chart);
    }




}
