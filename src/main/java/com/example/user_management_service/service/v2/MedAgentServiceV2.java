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

    public MedAgentGoalDTOV2 getGoalById(Long goalId) {
        MedAgentGoalV2 goal = medAgentGoalV2Repository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found with ID: " + goalId));

        return mapToGoalDTOV2(goal);
    }

    public MedAgentProfileDTOV2 getProfileByAgentId(UUID agentId) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Medical agent not found with ID: " + agentId));

        MedAgentGoalV2 activeGoal = medAgentGoalV2Repository.findActiveByAgentId(agentId, LocalDate.now())
                .orElse(null);

        // Calculate Sales Quote
        Long districtId = agent.getDistrict().getId();
        long totalSales =0;
        long targetSales = 0;
        SalesQuoteDTO salesQuoteDTO = new SalesQuoteDTO(totalSales, targetSales);

        MedAgentProfileKPIDTOV2 kpiDTO = calculateKPI(agentId);

        MedAgentGoalDTOV2 goalDTO = activeGoal != null ? mapToGoalDTOV2(activeGoal) : null;

        return new MedAgentProfileDTOV2(agentId, goalDTO, salesQuoteDTO, kpiDTO);
    }

    private MedAgentGoalDTOV2 mapToGoalDTOV2(MedAgentGoalV2 goal) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startDate = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        UUID agentId = goal.getMedAgent().getUserId();
        Long districtId = goal.getMedAgent().getDistrict().getId();

        // Medicine Quotes
        List<MedicineQuoteDTOV2> medicineQuotes = goal.getMedicineQuoteV2s().stream()
                .map(m -> {
                    Long amount = doctorContractV2Repository.findByCreatedByAndDistrict(agentId, districtId).stream()
                            .flatMap(c -> c.getMedicineWithQuantityDoctorV2s().stream())
                            .filter(med -> med.getMedicine().getId().equals(m.getMedicine().getId()))
                            .flatMap(med -> med.getContractMedicineDoctorAmountV2s().stream())
                            .filter(a -> a.getYearMonth().equals(currentMonth))
                            .mapToLong(ContractMedicineDoctorAmountV2::getAmount)
                            .sum();
                    return new MedicineQuoteDTOV2(m.getId(), m.getMedicine(), m.getQuote(), amount, currentMonth);
                })
                .collect(Collectors.toList());

        // Field Environment Quotes
        List<FieldEnvQuoteDTOV2> fieldEnvQuotes = goal.getFieldEnvV2s().stream()
                .map(f -> {
                    Long amount = (long) userRepository.findCreatedDoctorsThisMonthByDistrictAndMonth(districtId, f.getField(),   startDate,
                            endDate).size();
                    return new FieldEnvQuoteDTOV2(f.getId(), f.getField(), f.getQuote(), amount, currentMonth);
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

    private MedAgentProfileKPIDTOV2 calculateKPI(UUID agentId) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found with ID: " + agentId));
        Long districtId = agent.getDistrict().getId();
        if (districtId == null) {
            throw new IllegalStateException("District ID is null for agent ID: " + agentId);
        }

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startDate = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();
        // Total connected doctors (all time)
        long totalConnectedDoctors = doctorContractV2Repository.countByCreatedByAndDistrict(agentId, districtId);

        // Total connected contracts (all time)
        long totalConnectedContracts = doctorContractV2Repository.countByCreatedByAndDistrict(agentId, districtId);

        // Connected doctors this month
        long connectedDoctorsCurrentMonth = doctorContractV2Repository.countByCreatedByAndDistrictAndCreatedBetween(
                agentId, districtId, currentMonth);

        // Connected contracts this month
        long connectedContractsCurrentMonth = doctorContractV2Repository.countByCreatedByAndDistrictAndCreatedBetween(
                agentId, districtId, currentMonth);

        // Prescriptions issued this month
        long prescriptionsIssuedCurrentMonth = recipeRepository.countRecipesByDoctorsAssignedByMedAgentThisMonth(agentId);

        // Medications prescribed this month
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