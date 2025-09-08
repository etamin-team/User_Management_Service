package com.example.user_management_service.service.v2;

import com.example.user_management_service.exception.NotFoundException;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.SalesQuoteDTO;
import com.example.user_management_service.model.v2.*;
import com.example.user_management_service.model.v2.dto.FieldEnvQuoteDTOV2;
import com.example.user_management_service.model.v2.dto.MedAgentGoalDTOV2;
import com.example.user_management_service.model.v2.dto.MedAgentProfileDTOV2;
import com.example.user_management_service.model.v2.dto.MedAgentProfileKPIDTOV2;
import com.example.user_management_service.model.v2.dto.MedicineQuoteDTOV2;
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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-5:59 AM (EEST)
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

        // Handle Medicine Quotes via DoctorContractV2
        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            DoctorContractV2 contract = new DoctorContractV2();
            contract.setCreatedBy(agent);
            contract.setCreatedAt(LocalDate.now());
            contract.setStartDate(payload.getStartDate());
            contract.setEndDate(payload.getEndDate());
            contract.setStatus(payload.getGoalStatus() != null ? payload.getGoalStatus() : GoalStatus.PENDING_REVIEW);
            contract.setDoctor(agent); // Agent acts as doctor for simplicity, adjust if needed

            List<MedicineWithQuantityDoctorV2> medicineQuotes = payload.getMedicineQuotes().stream().map(quotePayload -> {
                MedicineWithQuantityDoctorV2 medicineQuote = new MedicineWithQuantityDoctorV2();
                medicineQuote.setMedicine(medicineRepository.findById(quotePayload.getMedicineId())
                        .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId())));
                medicineQuote.setQuote(quotePayload.getQuote());
                medicineQuote.setDoctorContract(contract);
                return medicineQuote;
            }).collect(Collectors.toList());
            contract.setMedicineWithQuantityDoctorV2s(medicineQuotes);
            goal.setDoctorContractV2s(List.of(contract));
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

        // Update or Append Medicine Quotes via DoctorContractV2
        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            DoctorContractV2 contract = goal.getDoctorContractV2s().stream()
                    .filter(c -> c.getCreatedBy().getUserId().equals(payload.getAgentId()))
                    .findFirst()
                    .orElseGet(() -> {
                        DoctorContractV2 newContract = new DoctorContractV2();
                        newContract.setCreatedBy(goal.getMedAgent());
                        newContract.setDoctor(goal.getMedAgent()); // Adjust if needed
                        newContract.setCreatedAt(LocalDate.now());
                        newContract.setStartDate(payload.getStartDate());
                        newContract.setEndDate(payload.getEndDate());
                        newContract.setStatus(payload.getGoalStatus() != null ? payload.getGoalStatus() : GoalStatus.PENDING_REVIEW);
                        goal.getDoctorContractV2s().add(newContract);
                        return newContract;
                    });

            for (MedicineQuotePayloadV2 quotePayload : payload.getMedicineQuotes()) {
                Optional<MedicineWithQuantityDoctorV2> existingQuote = contract.getMedicineWithQuantityDoctorV2s().stream()
                        .filter(q -> q.getMedicine().getId().equals(quotePayload.getMedicineId()))
                        .findFirst();
                if (existingQuote.isPresent()) {
                    existingQuote.get().setQuote(quotePayload.getQuote());
                } else {
                    MedicineWithQuantityDoctorV2 newQuote = new MedicineWithQuantityDoctorV2();
                    newQuote.setMedicine(medicineRepository.findById(quotePayload.getMedicineId())
                            .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId())));
                    newQuote.setQuote(quotePayload.getQuote());
                    newQuote.setDoctorContract(contract);
                    contract.getMedicineWithQuantityDoctorV2s().add(newQuote);
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
        long totalSales = activeGoal != null ? activeGoal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(agentId))
                .flatMap(c -> c.getMedicineWithQuantityDoctorV2s().stream())
                .flatMap(m -> m.getContractMedicineDoctorAmountV2s().stream())
                .filter(a -> a.getYearMonth().equals(YearMonth.now()))
                .mapToLong(ContractMedicineDoctorAmountV2::getAmount)
                .sum() : 0;
        long targetSales = activeGoal != null ? activeGoal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(agentId))
                .flatMap(c -> c.getMedicineWithQuantityDoctorV2s().stream())
                .mapToLong(MedicineWithQuantityDoctorV2::getQuote)
                .sum() : 0;
        SalesQuoteDTO salesQuoteDTO = new SalesQuoteDTO(totalSales, targetSales);

        MedAgentProfileKPIDTOV2 kpiDTO = calculateKPI(agentId);

        MedAgentGoalDTOV2 goalDTO = activeGoal != null ? mapToGoalDTOV2(activeGoal) : null;

        return new MedAgentProfileDTOV2(agentId, goalDTO, salesQuoteDTO, kpiDTO);
    }

    private MedAgentGoalDTOV2 mapToGoalDTOV2(MedAgentGoalV2 goal) {
        YearMonth currentMonth = YearMonth.now();
        List<MedicineQuoteDTOV2> medicineQuotes = goal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(goal.getMedAgent().getUserId()))
                .flatMap(c -> c.getMedicineWithQuantityDoctorV2s().stream())
                .map(m -> {
                    Long amount = m.getContractMedicineDoctorAmountV2s().stream()
                            .filter(a -> a.getYearMonth().equals(currentMonth))
                            .map(ContractMedicineDoctorAmountV2::getAmount)
                            .findFirst()
                            .orElse(0L);
                    return new MedicineQuoteDTOV2(m.getId(), m.getMedicine(), m.getQuote(), amount, currentMonth);
                })
                .collect(Collectors.toList());

        List<FieldEnvQuoteDTOV2> fieldEnvQuotes = goal.getFieldEnvV2s().stream()
                .map(f -> {
                    Long amount = f.getFieldEnvAmountV2().stream()
                            .filter(a -> a.getYearMonth().equals(currentMonth))
                            .map(FieldEnvAmountV2::getAmount)
                            .findFirst()
                            .orElse(0L);
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
        MedAgentGoalV2 activeGoal = medAgentGoalV2Repository.findActiveByAgentId(agentId, LocalDate.now())
                .orElse(null);

        long totalConnectedDoctors = activeGoal != null ? activeGoal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(agentId))
                .map(c -> c.getDoctor().getUserId())
                .distinct()
                .count() : 0;

        long totalConnectedContracts = activeGoal != null ? activeGoal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(agentId))
                .count() : 0;

        YearMonth currentMonth = YearMonth.now();
        long connectedDoctorsCurrentMonth = activeGoal != null ? activeGoal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(agentId)
                        && c.getStartDate().atStartOfDay().isBefore(currentMonth.atEndOfMonth().atTime(23, 59, 59))
                        && c.getEndDate().atStartOfDay().isAfter(currentMonth.atDay(1).atStartOfDay()))
                .map(c -> c.getDoctor().getUserId())
                .distinct()
                .count() : 0;

        long connectedContractsCurrentMonth = activeGoal != null ? activeGoal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(agentId)
                        && c.getStartDate().atStartOfDay().isBefore(currentMonth.atEndOfMonth().atTime(23, 59, 59))
                        && c.getEndDate().atStartOfDay().isAfter(currentMonth.atDay(1).atStartOfDay()))
                .count() : 0;

        List<UUID> doctorIds = activeGoal != null ? activeGoal.getDoctorContractV2s().stream()
                .filter(c -> c.getCreatedBy().getUserId().equals(agentId))
                .map(c -> c.getDoctor().getUserId())
                .distinct()
                .collect(Collectors.toList()) : new ArrayList<>();

        long prescriptionsIssuedCurrentMonth = doctorIds.isEmpty() ? 0 : recipeRepository
                .countByDoctorIdsAndMonth(doctorIds, currentMonth);

        long medicationsPrescribedCurrentMonth = doctorIds.isEmpty() ? 0 : recipeRepository
                .countMedicationsByDoctorIdsAndMonth(doctorIds, currentMonth);

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