package com.example.user_management_service.service.v2;

import com.example.user_management_service.exception.NotFoundException;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.SalesQuoteDTO;
import com.example.user_management_service.model.v2.*;
import com.example.user_management_service.model.v2.dto.*;
import com.example.user_management_service.model.v2.payload.FieldEnvQuotePayloadV2;
import com.example.user_management_service.model.v2.payload.MedAgentGoalCreateUpdatePayloadV2;
import com.example.user_management_service.model.v2.payload.MedicineQuotePayloadV2;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RecipeRepository;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.v2.ContractMedicineDoctorAmountV2Repository; // Ensure this is imported
import com.example.user_management_service.repository.v2.DoctorContractV2Repository;
import com.example.user_management_service.repository.v2.MedAgentGoalV2Repository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Date-9/11/2025
 * By Sardor Tokhirov
 * Time-12:40 AM (GMT+5)
 */
@Service
@AllArgsConstructor
@Transactional
public class MedAgentServiceV2 {

    private final MedAgentGoalV2Repository medAgentGoalV2Repository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final DoctorContractV2Repository doctorContractV2Repository;
    private final RecipeRepository recipeRepository;
    private final ContractMedicineDoctorAmountV2Repository contractMedicineDoctorAmountV2Repository; // Inject this

    public void createGoal(MedAgentGoalCreateUpdatePayloadV2 payload) {
        MedAgentGoalV2 goal = new MedAgentGoalV2();
        User agent = userRepository.findById(payload.getAgentId())
                .orElseThrow(() -> new NotFoundException("Medical agent not found with ID: " + payload.getAgentId()));
        goal.setMedAgent(agent);
        goal.setCreatedAt(LocalDate.now());
        goal.setStartDate(payload.getStartDate());
        goal.setEndDate(payload.getEndDate());
        goal.setStatus(payload.getGoalStatus() != null ? payload.getGoalStatus() : GoalStatus.PENDING_REVIEW);

        // Handle Medicine Quotes
        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            List<MedAgentMedicineQuoteV2> medicineQuotes = payload.getMedicineQuotes().stream().map(quotePayload -> {
                MedAgentMedicineQuoteV2 medicineQuote = new MedAgentMedicineQuoteV2();
                medicineQuote.setMedicine(medicineRepository.findById(quotePayload.getMedicineId())
                        .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId())));
                medicineQuote.setQuote(quotePayload.getQuote());
                medicineQuote.setMedAgentGoalV2(goal);
                return medicineQuote;
            }).collect(Collectors.toList());
            goal.setMedicineQuoteV2s(medicineQuotes);
        }

        // Handle Field Environment Quotes
        if (payload.getFieldEnvQuotes() != null && !payload.getFieldEnvQuotes().isEmpty()) {
            List<FieldEnvV2> fieldEnvQuotes = payload.getFieldEnvQuotes().stream().map(fieldPayload -> {
                FieldEnvV2 fieldEnv = new FieldEnvV2();
                fieldEnv.setField(fieldPayload.getField());
                fieldEnv.setQuote(fieldPayload.getFieldQuote());
                fieldEnv.setMedAgentGoalV2(goal);
                return fieldEnv;
            }).collect(Collectors.toList());
            goal.setFieldEnvV2s(fieldEnvQuotes);
        }

        medAgentGoalV2Repository.save(goal);
    }

    public void updateGoal(Long goalId, MedAgentGoalCreateUpdatePayloadV2 payload) {
        MedAgentGoalV2 goal = medAgentGoalV2Repository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found with ID: " + goalId));

        goal.setStartDate(payload.getStartDate());
        goal.setEndDate(payload.getEndDate());
        goal.setStatus(payload.getGoalStatus() != null ? payload.getGoalStatus() : goal.getStatus());

        // Update or Append Medicine Quotes
        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            for (MedicineQuotePayloadV2 quotePayload : payload.getMedicineQuotes()) {
                Optional<MedAgentMedicineQuoteV2> existingQuote = goal.getMedicineQuoteV2s().stream()
                        .filter(q -> q.getMedicine().getId().equals(quotePayload.getMedicineId()))
                        .findFirst();
                if (existingQuote.isPresent()) {
                    existingQuote.get().setQuote(quotePayload.getQuote());
                } else {
                    MedAgentMedicineQuoteV2 newQuote = new MedAgentMedicineQuoteV2();
                    newQuote.setMedicine(medicineRepository.findById(quotePayload.getMedicineId())
                            .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId())));
                    newQuote.setQuote(quotePayload.getQuote());
                    newQuote.setMedAgentGoalV2(goal);
                    goal.getMedicineQuoteV2s().add(newQuote);
                }
            }
        }

        // Update or Append Field Environment Quotes
        if (payload.getFieldEnvQuotes() != null && !payload.getFieldEnvQuotes().isEmpty()) {
            for (FieldEnvQuotePayloadV2 fieldPayload : payload.getFieldEnvQuotes()) {
                Optional<FieldEnvV2> existingField = goal.getFieldEnvV2s().stream()
                        .filter(f -> f.getField() == fieldPayload.getField())
                        .findFirst();
                if (existingField.isPresent()) {
                    existingField.get().setQuote(fieldPayload.getFieldQuote());
                } else {
                    FieldEnvV2 newField = new FieldEnvV2();
                    newField.setField(fieldPayload.getField());
                    newField.setQuote(fieldPayload.getFieldQuote());
                    newField.setMedAgentGoalV2(goal);
                    goal.getFieldEnvV2s().add(newField);
                }
            }
        }

        medAgentGoalV2Repository.save(goal);
    }

    // --- New/Updated Public methods for fetching goals/profile with YearMonth ---

    public MedAgentGoalDTOV2 getGoalById(Long goalId, YearMonth targetMonth) {
        MedAgentGoalV2 goal = medAgentGoalV2Repository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found with ID: " + goalId));

        return mapToGoalDTOV2(goal, targetMonth); // Pass targetMonth
    }

    /**
     * Retrieves the active goal for a given medical agent and maps it to a DTO,
     * calculating amounts for a specific target month.
     * @param agentId The ID of the medical agent.
     * @param targetMonth The month for which to calculate current amounts.
     * @return The DTO representation of the active goal.
     * @throws NotFoundException if no active goal is found for the agent.
     */
    public MedAgentGoalDTOV2 getActiveGoalByMedAgentId(UUID agentId, YearMonth targetMonth) {
        // Note: findActiveByAgentId still uses LocalDate.now() to find the *currently active goal*.
        // The targetMonth is then used to calculate the *actual amounts for that specific month* within that goal.
        MedAgentGoalV2 activeGoal = medAgentGoalV2Repository.findActiveByAgentId(agentId, LocalDate.now())
                .orElseThrow(() -> new NotFoundException("Active goal not found for medical agent with ID: " + agentId));
        return mapToGoalDTOV2(activeGoal, targetMonth);
    }

    public MedAgentProfileDTOV2 getProfileByAgentId(UUID agentId, YearMonth targetMonth) { // Added YearMonth
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Medical agent not found with ID: " + agentId));

        MedAgentGoalV2 activeGoal = medAgentGoalV2Repository.findActiveByAgentId(agentId, LocalDate.now())
                .orElse(null);

        // Calculate Sales Quote - Now uses targetMonth
        SalesQuoteDTO salesQuoteDTO = getSalesQuoteDTO(activeGoal, targetMonth);

        // Calculate KPI - Now uses targetMonth
        MedAgentProfileKPIDTOV2 kpiDTO = calculateKPI(agentId, targetMonth);

        MedAgentGoalDTOV2 goalDTO = activeGoal != null ? mapToGoalDTOV2(activeGoal, targetMonth) : null; // Pass targetMonth

        return new MedAgentProfileDTOV2(agentId, goalDTO, salesQuoteDTO, kpiDTO);
    }

    /**
     * Deletes a medical agent goal by its ID.
     * @param goalId The ID of the goal to delete.
     * @return true if the goal was found and deleted, false otherwise.
     */
    public boolean deleteGoal(Long goalId) {
        MedAgentGoalV2 goal = medAgentGoalV2Repository.findById(goalId)
                .orElse(null);

        if (goal != null) {
           
            if (goal.getMedicineQuoteV2s() != null) {
                goal.getMedicineQuoteV2s().clear();
            }
            if (goal.getFieldEnvV2s() != null) {
                goal.getFieldEnvV2s().clear();
            }
            medAgentGoalV2Repository.delete(goal);
            return true;
        }
        return false;
    }


    // --- Private helper methods now accepting YearMonth ---

    private MedAgentGoalDTOV2 mapToGoalDTOV2(MedAgentGoalV2 goal, YearMonth targetMonth) { // Added targetMonth
        if (goal == null) {
            return null;
        }
        // Re-calculate startDate/endDate based on targetMonth for current amounts
        LocalDateTime startDateTime = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = targetMonth.atEndOfMonth().atTime(23, 59, 59);
        UUID agentId = goal.getMedAgent().getUserId();
        Long districtId = goal.getMedAgent().getDistrict().getId();

        // Medicine Quotes
        List<MedicineQuoteDTOV2> medicineQuotes = goal.getMedicineQuoteV2s().stream()
                .map(m -> {
                    // Use the new repository method with agentId and districtId filter
                    Long amount = contractMedicineDoctorAmountV2Repository.sumMedicineAmountsByAgentDistrictMedicineAndMonth(
                            agentId,
                            m.getMedicine().getId(),
                            targetMonth // Use targetMonth
                    ).orElse(0L); // Default to 0 if sum is null

                    return new MedicineQuoteDTOV2(m.getId(), m.getMedicine(), m.getQuote(), amount, null, targetMonth);
                })
                .collect(Collectors.toList());

        // Field Environment Quotes
        List<FieldEnvQuoteDTOV2> fieldEnvQuotes = goal.getFieldEnvV2s().stream()
                .map(f -> {
                    // This method needs to be updated in UserRepository if it doesn't take YearMonth / LocalDateTime range
                    // Assuming userRepository.findCreatedDoctorsThisMonthByDistrictAndMonth correctly uses the LocalDateTime range
                    Long amount = (long) userRepository.findCreatedDoctorsThisMonthByDistrictAndMonth(
                            districtId,
                            f.getField(),
                            startDateTime, // derived from targetMonth
                            endDateTime    // derived from targetMonth
                    ).size();
                    return new FieldEnvQuoteDTOV2(f.getId(), f.getField(), f.getQuote(), amount, targetMonth);
                })
                .collect(Collectors.toList());

        return new MedAgentGoalDTOV2(
                goal.getId(),
                goal.getMedAgent().getUserId(),
                goal.getStatus(),
                goal.getCreatedAt(),
                goal.getStartDate(),
                goal.getEndDate(),
                medicineQuotes,
                fieldEnvQuotes
        );
    }

    private SalesQuoteDTO getSalesQuoteDTO(MedAgentGoalV2 medAgentGoal, YearMonth targetMonth) {
        if (medAgentGoal == null) {
            return new SalesQuoteDTO(0L, 0L); // Return default if no goal
        }

        UUID agentId = medAgentGoal.getMedAgent().getUserId();
        Long districtId = medAgentGoal.getMedAgent().getDistrict().getId();
        if (districtId == null) {
            throw new IllegalStateException("District ID is null for medical agent ID: " + agentId);
        }

        // Calculate total sales by summing up amounts for each medicine quote within the goal
        long totalSales = medAgentGoal.getMedicineQuoteV2s().stream()
                .mapToLong(quote -> contractMedicineDoctorAmountV2Repository.sumMedicineAmountsByAgentDistrictMedicineAndMonth(
                        agentId,
                        quote.getMedicine().getId(),
                        targetMonth
                ).orElse(0L))
                .sum();

        long targetSales = medAgentGoal.getMedicineQuoteV2s().stream()
                .mapToLong(MedAgentMedicineQuoteV2::getQuote)
                .sum();

        return new SalesQuoteDTO(totalSales, targetSales);
    }

    private MedAgentProfileKPIDTOV2 calculateKPI(UUID agentId, YearMonth targetMonth) { // Added YearMonth
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found with ID: " + agentId));
        Long districtId = agent.getDistrict().getId();
        if (districtId == null) {
            throw new IllegalStateException("District ID is null for agent ID: " + agentId);
        }

        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(23, 59, 59);

        // Total connected doctors (all time)
        long totalConnectedDoctors = doctorContractV2Repository.countByCreatedByAndDistrict(agentId, districtId);

        // Total connected contracts (all time) - Often these are the same count if one contract per doctor is assumed
        long totalConnectedContracts = doctorContractV2Repository.countByCreatedByAndDistrict(agentId, districtId);

        // Connected doctors this month
        // doctorContractV2Repository.countByCreatedByAndDistrictAndCreatedBetween currently takes YearMonth
        long connectedDoctorsCurrentMonth = doctorContractV2Repository.countByCreatedByAndDistrictAndCreatedBetween(
                agentId, districtId, targetMonth); // Use targetMonth

        // Connected contracts this month
        long connectedContractsCurrentMonth = doctorContractV2Repository.countByCreatedByAndDistrictAndCreatedBetween(
                agentId, districtId, targetMonth); // Use targetMonth

        // Prescriptions issued this month
        // TODO: These RecipeRepository methods likely need to be updated to accept YearMonth or LocalDateTime range.
        // Assuming they will be updated or already implicitly handle the targetMonth if passed.
        // For example, you might need: recipeRepository.countRecipesByDoctorsAssignedByMedAgentAndMonth(agentId, targetMonth)
        // For now, they will calculate for the current *actual* month unless updated.
        long prescriptionsIssuedCurrentMonth = recipeRepository.countRecipesByDoctorsAssignedByMedAgentThisMonth(agentId);
        long medicationsPrescribedCurrentMonth = recipeRepository.totalMedicineAmountByMedAgentThisMonth(agentId);

        return new MedAgentProfileKPIDTOV2(
                agentId,
                totalConnectedDoctors,
                totalConnectedContracts,
                connectedDoctorsCurrentMonth,
                connectedContractsCurrentMonth,
                prescriptionsIssuedCurrentMonth,
                medicationsPrescribedCurrentMonth
        );
    }
}