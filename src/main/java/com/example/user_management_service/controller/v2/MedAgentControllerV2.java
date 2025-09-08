package com.example.user_management_service.controller.v2;

import com.example.user_management_service.model.v2.dto.MedAgentGoalDTOV2;
import com.example.user_management_service.model.v2.dto.MedAgentProfileDTOV2;
import com.example.user_management_service.model.v2.payload.MedAgentGoalCreateUpdatePayloadV2;
import com.example.user_management_service.service.v2.MedAgentServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-5:59 AM (EEST)
 */
@RestController
@RequestMapping("/api/v2/med-agent")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class MedAgentControllerV2 {

    private final MedAgentServiceV2 medAgentServiceV2;

    @PostMapping("/new-goal")
    public ResponseEntity<Void> createGoal(@RequestBody MedAgentGoalCreateUpdatePayloadV2 payload) {
        medAgentServiceV2.createGoal(payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PutMapping("/goal/{goalId}")
    public ResponseEntity<Void> updateGoal(@PathVariable Long goalId, @RequestBody MedAgentGoalCreateUpdatePayloadV2 payload) {
        medAgentServiceV2.updateGoal(goalId, payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<MedAgentGoalDTOV2> getGoalById(@PathVariable Long goalId) {
        MedAgentGoalDTOV2 goal = medAgentServiceV2.getGoalById(goalId);
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/profile/{agentId}")
    public ResponseEntity<MedAgentProfileDTOV2> getProfileByAgentId(@PathVariable UUID agentId) {
        MedAgentProfileDTOV2 profile = medAgentServiceV2.getProfileByAgentId(agentId);
        return ResponseEntity.ok(profile);
    }
}