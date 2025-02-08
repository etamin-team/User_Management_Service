package com.example.user_management_service.controller;

import com.example.user_management_service.model.Recipe;
import com.example.user_management_service.model.Template;
import com.example.user_management_service.model.dto.ContractAmountDTO;
import com.example.user_management_service.model.dto.OutOfContractAmountDTO;
import com.example.user_management_service.model.dto.RecipeDto;
import com.example.user_management_service.model.dto.TemplateDto;
import com.example.user_management_service.service.ContractService;
import com.example.user_management_service.service.DoctorService;
import com.example.user_management_service.service.RecipeService;
import com.example.user_management_service.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/templates")
    public List<TemplateDto> getTemplates(
            @RequestParam(required = false) Boolean saved,
            @RequestParam(required = false, defaultValue = "false") Boolean sortBy,
            @RequestParam(required = false) String searchText) {
        return doctorService.getTemplates(saved, sortBy, searchText,roleService.getCurrentUserId());
    }



    @PostMapping("/save-recipe")
    public void saveRecipe(@RequestBody RecipeDto recipe) {
        recipeService.saveRecipe(recipe);
    }

    @GetMapping("/contract/contract-id/{contractId}")
    public ResponseEntity<ContractAmountDTO> getContractById(@PathVariable Long contractId) {
        ContractAmountDTO contractAmountDTO = contractService.getContractById(contractId);
        return ResponseEntity.ok(contractAmountDTO);
    }
    @GetMapping("/contract/doctor-id/{doctorId}")
    public ResponseEntity<ContractAmountDTO> getContractByDoctorId(@PathVariable UUID doctorId) {
        ContractAmountDTO contractAmountDTO = contractService.getContractByDoctorId(doctorId);
        return ResponseEntity.ok(contractAmountDTO);
    }

    @GetMapping("/out-contract/doctor-id/{doctorId}")
    public ResponseEntity<OutOfContractAmountDTO> getOutOfContractByDoctorId(@PathVariable UUID doctorId) {
        OutOfContractAmountDTO contractAmountDTO = contractService.getOutOfContractsByDoctorId(doctorId);
        return ResponseEntity.ok(contractAmountDTO);
    }

}
