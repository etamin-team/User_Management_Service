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
import com.example.user_management_service.repository.v2.ContractMedicineDoctorAmountV2Repository;
import com.example.user_management_service.repository.v2.DoctorContractV2Repository;
import com.example.user_management_service.repository.v2.MedAgentGoalV2Repository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ContractMedicineDoctorAmountV2Repository contractMedicineDoctorAmountV2Repository;

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

    public MedAgentGoalDTOV2 getGoalById(Long goalId, YearMonth targetMonth) {
        MedAgentGoalV2 goal = medAgentGoalV2Repository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found with ID: " + goalId));

        return mapToGoalDTOV2(goal, targetMonth);
    }

    public MedAgentGoalDTOV2 getActiveGoalByMedAgentId(UUID agentId, YearMonth targetMonth) {
        MedAgentGoalV2 activeGoal = medAgentGoalV2Repository.findActiveByAgentId(agentId, LocalDate.now())
                .orElseThrow(() -> new NotFoundException("Active goal not found for medical agent with ID: " + agentId));
        return mapToGoalDTOV2(activeGoal, targetMonth);
    }

    public MedAgentProfileDTOV2 getProfileByAgentId(UUID agentId, YearMonth targetMonth) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Medical agent not found with ID: " + agentId));

        MedAgentGoalV2 activeGoal = medAgentGoalV2Repository.findActiveByAgentId(agentId, LocalDate.now())
                .orElse(null);

        SalesQuoteDTO salesQuoteDTO = getSalesQuoteDTO(activeGoal, targetMonth);

        MedAgentProfileKPIDTOV2 kpiDTO = calculateKPI(agentId, targetMonth);

        MedAgentGoalDTOV2 goalDTO = activeGoal != null ? mapToGoalDTOV2(activeGoal, targetMonth) : null;

        return new MedAgentProfileDTOV2(agentId, goalDTO, salesQuoteDTO, kpiDTO);
    }

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

    // --- New `getAllContractsByAgent` method with filters ---
    public Page<ContractDTOV2> getAllContractsByAgent(
            UUID agentId,
            Long regionId,
            Long districtId,
            Long workPlaceId,
            String firstName,
            String lastName,
            String middleName,
            Field fieldName,
            int page,
            int size,
            YearMonth targetMonth
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Call the repository method with all filter parameters
        return doctorContractV2Repository.findAllContractsByAgent(
                agentId,
                regionId,
                districtId,
                workPlaceId,
                firstName,
                lastName,
                middleName,
                fieldName,
                pageable
        ).map(contract -> mapToContractDTOV2(contract, targetMonth)); // Apply targetMonth when mapping
    }

    private MedAgentGoalDTOV2 mapToGoalDTOV2(MedAgentGoalV2 goal, YearMonth targetMonth) {
        if (goal == null) {
            return null;
        }
        LocalDateTime startDateTime = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = targetMonth.atEndOfMonth().atTime(23, 59, 59);
        UUID agentId = goal.getMedAgent().getUserId();
        Long districtId = goal.getMedAgent().getDistrict().getId();

        List<MedicineQuoteDTOV2> medicineQuotes = goal.getMedicineQuoteV2s().stream()
                .map(m -> {
                    Long amount = contractMedicineDoctorAmountV2Repository.sumMedicineAmountsByAgentDistrictMedicineAndMonth(
                            agentId,
                            m.getMedicine().getId(),
                            targetMonth
                    ).orElse(0L);

                    return new MedicineQuoteDTOV2(m.getId(), m.getMedicine(), m.getQuote(), amount, null, targetMonth);
                })
                .collect(Collectors.toList());

        List<FieldEnvQuoteDTOV2> fieldEnvQuotes = goal.getFieldEnvV2s().stream()
                .map(f -> {
                    Long amount = (long) userRepository.findCreatedDoctorsThisMonthByDistrictAndMonth(
                            districtId,
                            f.getField(),
                            startDateTime,
                            endDateTime
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
            return new SalesQuoteDTO(0L, 0L);
        }

        UUID agentId = medAgentGoal.getMedAgent().getUserId();
        Long districtId = medAgentGoal.getMedAgent().getDistrict().getId();
        if (districtId == null) {
            throw new IllegalStateException("District ID is null for medical agent ID: " + agentId);
        }

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

    private MedAgentProfileKPIDTOV2 calculateKPI(UUID agentId, YearMonth targetMonth) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found with ID: " + agentId));
        Long districtId = agent.getDistrict().getId();
        if (districtId == null) {
            throw new IllegalStateException("District ID is null for agent ID: " + agentId);
        }

        long totalConnectedDoctors = doctorContractV2Repository.countByCreatedByAndDistrict(agentId, districtId);

        long totalConnectedContracts = doctorContractV2Repository.countByCreatedByAndDistrict(agentId, districtId);

        long connectedDoctorsCurrentMonth = doctorContractV2Repository.countByCreatedByAndDistrictAndCreatedBetween(
                agentId, districtId, targetMonth);

        long connectedContractsCurrentMonth = doctorContractV2Repository.countByCreatedByAndDistrictAndCreatedBetween(
                agentId, districtId, targetMonth);

        // Note: These RecipeRepository methods likely need to be updated to accept YearMonth or LocalDateTime range
        // if you want them to respect `targetMonth`. Currently, their names suggest "ThisMonth" (i.e., YearMonth.now()).
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

    // This `mapToContractDTOV2` is specifically for mapping `DoctorContractV2` to `ContractDTOV2`
    // and is used by `getAllContractsByAgent`.
    private ContractDTOV2 mapToContractDTOV2(DoctorContractV2 contract, YearMonth targetMonth) {
        List<MedicineQuoteDTOV2> medicineQuotes = contract.getMedicineWithQuantityDoctorV2s().stream()
                .map(m -> {
                    Optional<ContractMedicineDoctorAmountV2> amountEntry = m.getContractMedicineDoctorAmountV2s().stream()
                            .filter(a -> a.getYearMonth().equals(targetMonth))
                            .findFirst();

                    Long amount = amountEntry.map(ContractMedicineDoctorAmountV2::getAmount).orElse(0L);
                    Long correction = amountEntry.map(ContractMedicineDoctorAmountV2::getCorrection).orElse(0L);

                    return new MedicineQuoteDTOV2(m.getId(), m.getMedicine(), m.getQuote(), amount, correction, targetMonth);
                })
                .collect(Collectors.toList());

        return new ContractDTOV2(
                contract.getId(),
                contract.getCreatedBy() != null ? contract.getCreatedBy().getUserId() : null,
                contract.getDoctor().getUserId(),
                contract.getCreatedAt(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getStatus(),
                contract.getContractType(),
                medicineQuotes
        );
    }
}