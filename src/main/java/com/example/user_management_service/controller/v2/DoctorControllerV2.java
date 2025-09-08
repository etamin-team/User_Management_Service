package com.example.user_management_service.controller.v2;


import com.example.user_management_service.model.dto.RecipeDto;
import com.example.user_management_service.model.v2.dto.ContractDTOV2;
import com.example.user_management_service.model.v2.dto.DoctorProfileDTOV2;
import com.example.user_management_service.model.v2.payload.DoctorContractCreateUpdatePayloadV2;
import com.example.user_management_service.service.v2.DoctorServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ContractDTOV2> getContractById(@PathVariable Long contractId) {
        ContractDTOV2 contract = doctorService2.getContractById(contractId);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/contract/doctor-id/{doctorId}")
    public ResponseEntity<ContractDTOV2> getContractByDoctorId(@PathVariable UUID doctorId) {
        ContractDTOV2 contract = doctorService2.getContractByDoctorId(doctorId);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/profile/{doctorId}")
    public ResponseEntity<DoctorProfileDTOV2> getDoctorProfileByDoctorId(@PathVariable UUID doctorId) {
        DoctorProfileDTOV2 doctorProfileDTO = doctorService2.getDoctorProfileByDoctorId(doctorId);
        return ResponseEntity.ok(doctorProfileDTO);
    }

    @PostMapping("/save-recipe")
    public ResponseEntity<Boolean> saveRecipe(@RequestBody RecipeDto recipe) {
        doctorService2.saveRecipe(recipe);
        return ResponseEntity.ok(true);
    }
}