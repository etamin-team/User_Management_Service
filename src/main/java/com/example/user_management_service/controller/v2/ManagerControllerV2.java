package com.example.user_management_service.controller.v2;

import com.example.user_management_service.model.v2.dto.ManagerGoalDTOV2;
import com.example.user_management_service.model.v2.dto.ManagerProfileDTOV2;
import com.example.user_management_service.model.v2.payload.ManagerGoalCreateUpdatePayloadV2;
import com.example.user_management_service.service.v2.ManagerServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat; // Import this
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Optional; // Import this
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/manager")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ManagerControllerV2 {

    private final ManagerServiceV2 managerServiceV2;

    @PostMapping("/manager/new-goal")
    public ResponseEntity<Void> createManagerGoal(@RequestBody ManagerGoalCreateUpdatePayloadV2 managerGoalCreateUpdatePayloadV2) {
        managerServiceV2.createManagerGoal(managerGoalCreateUpdatePayloadV2);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PutMapping("/manager/goal/{id}")
    public ResponseEntity<Void> updateManagerGoal(
            @PathVariable Long id,
            @RequestBody ManagerGoalCreateUpdatePayloadV2 managerGoalCreateUpdatePayloadV2
    ) {
        managerServiceV2.updateManagerGoal(id, managerGoalCreateUpdatePayloadV2);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/manager/goal/{id}")
    public ResponseEntity<Void> deleteManagerGoal(@PathVariable Long id) {
        if (managerServiceV2.deleteManagerGoal(id)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/manager/goal/{goalId}")
    public ResponseEntity<ManagerGoalDTOV2> getManagerGoalById(
            @PathVariable Long goalId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        // If yearMonth is provided, use it; otherwise, default to current month
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        ManagerGoalDTOV2 managerGoalDTO = managerServiceV2.getManagerGoalById(goalId, targetMonth);
        return ResponseEntity.ok(managerGoalDTO);
    }

    @GetMapping("/manager/profile/{managerId}")
    public ResponseEntity<ManagerProfileDTOV2> getManagerProfileByManagerId(
            @PathVariable UUID managerId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        // If yearMonth is provided, use it; otherwise, default to current month
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        ManagerProfileDTOV2 managerProfile = managerServiceV2.getManagerProfileByManagerId(managerId, targetMonth);
        return ResponseEntity.ok(managerProfile);
    }

    @GetMapping("/manager/{managerId}/goal")
    public ResponseEntity<ManagerGoalDTOV2> getManagerGoalByManagerId(
            @PathVariable UUID managerId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") Optional<YearMonth> yearMonth
    ) {
        // If yearMonth is provided, use it; otherwise, default to current month
        YearMonth targetMonth = yearMonth.orElse(YearMonth.now());
        ManagerGoalDTOV2 managerGoal = managerServiceV2.getManagerGoalByManagerId(managerId, targetMonth);
        return managerGoal != null ? ResponseEntity.ok(managerGoal) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}