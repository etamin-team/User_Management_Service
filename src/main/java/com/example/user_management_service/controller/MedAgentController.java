package com.example.user_management_service.controller;

import com.example.user_management_service.model.dto.AgentContractDTO;
import com.example.user_management_service.model.dto.ContractAmountDTO;
import com.example.user_management_service.model.dto.ContractDTO;
import com.example.user_management_service.model.dto.MedAgentStatusDTO;
import com.example.user_management_service.service.AdminService;
import com.example.user_management_service.service.ContractService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Date-2/9/2025
 * By Sardor Tokhirov
 * Time-2:26 PM (GMT+5)
 */

@RestController
@RequestMapping("/api/v1/med-agent")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class MedAgentController {

    private final AdminService adminService;
    private final ContractService contractService;


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


    @GetMapping("/doctor/contract/contract-id/{contractId}")
    public ResponseEntity<ContractAmountDTO> getContractById(@PathVariable Long contractId) {
        ContractAmountDTO contractAmountDTO = contractService.getContractById(contractId);
        return ResponseEntity.ok(contractAmountDTO);
    }
    @GetMapping("/doctor/contract/doctor-id/{doctorId}")
    public ResponseEntity<ContractAmountDTO> getContractByDoctorId(@PathVariable UUID doctorId) {
        ContractAmountDTO contractAmountDTO = contractService.getContractByDoctorId(doctorId);
        return ResponseEntity.ok(contractAmountDTO);
    }

    @GetMapping("/goal/goal-id/{agentGoalId}")
    public ResponseEntity<AgentContractDTO> getAgentGoalById(@PathVariable Long agentGoalId) {
        AgentContractDTO agentContractDTO = adminService.getAgentGoalById(agentGoalId);
        return ResponseEntity.ok(agentContractDTO);
    }

    @GetMapping("/goal/agent-id/{medAgentId}")
    public ResponseEntity<AgentContractDTO> getAgentGoalMedAgentId(@PathVariable UUID medAgentId) {
        AgentContractDTO agentContractDTO = adminService.getAgentGoalByMedAgentId(medAgentId);
        return ResponseEntity.ok(agentContractDTO);
    }

    @GetMapping("/statistics/{medAgentId}")
    public ResponseEntity<MedAgentStatusDTO> getDoctorRecipeStatsDTOByMedAgentId(@PathVariable UUID medAgentId) {
        MedAgentStatusDTO contractAmountDTO = adminService.getMedAgentStatusInfo(medAgentId);
        return ResponseEntity.ok(contractAmountDTO);
    }

    @GetMapping("/{medAgentId}/contracts")
    public ResponseEntity<List<ContractDTO>> getContractsByMedAgent(@PathVariable UUID medAgentId) {
        List<ContractDTO> contracts = contractService.getContractsByMedAgent(medAgentId);
        return ResponseEntity.ok(contracts);
    }
}
