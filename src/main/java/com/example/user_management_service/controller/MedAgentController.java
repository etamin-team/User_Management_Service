//package com.example.user_management_service.controller;
//
//
//import com.example.user_management_service.model.dto.AgentContractDTO;
//import com.example.user_management_service.model.dto.MedAgentStatusDTO;
//import com.example.user_management_service.service.AdminService;
//import com.example.user_management_service.service.ContractService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * Date-2/9/2025
// * By Sardor Tokhirov
// * Time-2:26 PM (GMT+5)
// */
//
//@RestController
//@RequestMapping("/api/v1/med-agent")
//@CrossOrigin(origins = "*")
//@AllArgsConstructor
//public class MedAgentController {
//
//    private final AdminService adminService;
//    private final ContractService contractService;
//
//
//
//
//    @GetMapping("/goal/goal-id/{agentGoalId}")
//    public ResponseEntity<AgentContractDTO> getAgentGoalById(@PathVariable Long agentGoalId) {
//        AgentContractDTO agentContractDTO = adminService.getAgentGoalById(agentGoalId);
//        return ResponseEntity.ok(agentContractDTO);
//    }
//
//    @GetMapping("/goal/agent-id/{medAgentId}")
//    public ResponseEntity<AgentContractDTO> getAgentGoalMedAgentId(@PathVariable UUID medAgentId) {
//        AgentContractDTO agentContractDTO = adminService.getAgentGoalByMedAgentId(medAgentId);
//        return ResponseEntity.ok(agentContractDTO);
//    }
//
//    @GetMapping("/statistics/{medAgentId}")
//    public ResponseEntity<MedAgentStatusDTO> getDoctorRecipeStatsDTOByMedAgentId(@PathVariable UUID medAgentId) {
//        MedAgentStatusDTO contractAmountDTO = adminService.getMedAgentStatusInfo(medAgentId);
//        return ResponseEntity.ok(contractAmountDTO);
//    }
//
//
//}
