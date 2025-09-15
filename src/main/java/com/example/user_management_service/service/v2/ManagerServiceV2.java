package com.example.user_management_service.service.v2;

import com.example.user_management_service.exception.ManagerGoalException;
import com.example.user_management_service.exception.NotFoundException;
import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.DistrictDTO;
import com.example.user_management_service.model.dto.SalesQuoteDTO;
import com.example.user_management_service.model.v2.*;
import com.example.user_management_service.model.v2.dto.*;
import com.example.user_management_service.model.v2.payload.*;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RecipeRepository;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.v2.*;
import com.example.user_management_service.role.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerServiceV2 {

    private final ManagerGoalV2Repository managerGoalV2Repository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final ContractMedicineDoctorAmountV2Repository contractMedicineDoctorAmountV2Repository;
    private final MedicineQuoteV2Repository medicineQuoteV2Repository;
    private final FieldEnvQuoteV2Repository fieldEnvQuoteV2Repository;
    private final MedAgentEnvV2Repository medAgentEnvV2Repository;
    private final DistrictRepository districtRepository;
    private final DoctorContractV2Repository doctorContractV2Repository;
    private final RecipeRepository recipeRepository;
    private final MedAgentGoalV2Repository medAgentGoalV2Repository;

    // --- Public methods receiving YearMonth ---
    public ManagerGoalDTOV2 getManagerGoalByManagerId(UUID managerId, YearMonth targetMonth) {
        // Note: The getGoalsByManagerId repository method still uses LocalDate.now().
        // This means it fetches the goal that is currently active.
        // The `targetMonth` parameter is then used to calculate metrics *within* that active goal for a specific month.
        ManagerGoalV2 managerGoal = managerGoalV2Repository.getGoalsByManagerId(managerId, LocalDate.now())
                .orElseThrow(() -> new NotFoundException("Manager goal not found for manager ID: " + managerId));
        return convertToManagerGoalDTOV2(managerGoal, targetMonth); // Pass targetMonth
    }

    public ManagerGoalDTOV2 getManagerGoalById(Long goalId, YearMonth targetMonth) {
        ManagerGoalV2 managerGoal = managerGoalV2Repository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Manager goal not found with ID: " + goalId));
        return convertToManagerGoalDTOV2(managerGoal, targetMonth); // Pass targetMonth
    }

    public ManagerProfileDTOV2 getManagerProfileByManagerId(UUID managerId, YearMonth targetMonth) {
        ManagerGoalV2 managerGoal = managerGoalV2Repository.getGoalsByManagerId(managerId, LocalDate.now())
                .orElse(null);
        ManagerProfileDTOV2 profileDTO = new ManagerProfileDTOV2();
        profileDTO.setManagerId(managerId);
        profileDTO.setManagerGoalDTOV2(convertToManagerGoalDTOV2(managerGoal, targetMonth)); // Pass targetMonth
        profileDTO.setSalesQuoteDTO(getSalesQuoteDTO(managerGoal, targetMonth)); // Pass targetMonth
        profileDTO.setManagerProfileKPIDTOV2(getManagerProfileKPIDTOV2(managerId, targetMonth)); // Pass targetMonth
        return profileDTO;
    }

    // --- Other existing public methods (no change) ---
    public void createManagerGoal(ManagerGoalCreateUpdatePayloadV2 payload) {
        if (managerGoalV2Repository.getGoalsByManagerId(payload.getManagerId(), LocalDate.now()).isPresent()) {
            throw new ManagerGoalException("Manager has already assigned goal for ID: " + payload.getManagerId());
        }
        ManagerGoalV2 managerGoal = new ManagerGoalV2();
        managerGoal.setManagerId(userRepository.findById(payload.getManagerId())
                .orElseThrow(() -> new NotFoundException("Manager not found")));
        managerGoal.setCreatedAt(LocalDate.now());
        managerGoal.setStartDate(payload.getStartDate());
        managerGoal.setEndDate(payload.getEndDate());
        managerGoal.setStatus(payload.getGoalStatus() != null ? payload.getGoalStatus() : GoalStatus.APPROVED);

        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            List<MedicineQuoteV2> medicineQuotes = payload.getMedicineQuotes().stream().map(quotePayload -> {
                MedicineQuoteV2 medicineQuote = new MedicineQuoteV2();
                medicineQuote.setMedicine(medicineRepository.findById(quotePayload.getMedicineId())
                        .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId())));
                medicineQuote.setQuote(quotePayload.getQuote());
                medicineQuote.setManagerGoalV2(managerGoal);
                return medicineQuote;
            }).collect(Collectors.toList());
            managerGoal.setMedicineQuoteV2s(medicineQuotes);
        }

        if (payload.getFieldEnvQuotes() != null && !payload.getFieldEnvQuotes().isEmpty()) {
            List<FieldEnvQuoteV2> fieldEnvQuotes = payload.getFieldEnvQuotes().stream().map(fieldPayload -> {
                FieldEnvQuoteV2 fieldEnvQuote = new FieldEnvQuoteV2();
                fieldEnvQuote.setField(fieldPayload.getField());
                fieldEnvQuote.setQuote(fieldPayload.getFieldQuote());
                fieldEnvQuote.setManagerGoalV2(managerGoal);
                return fieldEnvQuote;
            }).collect(Collectors.toList());
            managerGoal.setFieldEnvQuoteV2s(fieldEnvQuotes);
        }

        if (payload.getMedAgenEnvQuotes() != null && !payload.getMedAgenEnvQuotes().isEmpty()) {
            List<MedAgentEnvV2> medAgentEnvs = payload.getMedAgenEnvQuotes().stream().map(agentPayload -> {
                MedAgentEnvV2 medAgentEnv = new MedAgentEnvV2();
                medAgentEnv.setDistrict(districtRepository.findById(agentPayload.getDistrictId())
                        .orElseThrow(() -> new NotFoundException("District not found with ID: " + agentPayload.getDistrictId())));
                medAgentEnv.setQuote(agentPayload.getQuote());
                medAgentEnv.setManagerGoalV2(managerGoal);
                return medAgentEnv;
            }).collect(Collectors.toList());
            managerGoal.setMedAgentEnvs(medAgentEnvs);
        }

        managerGoalV2Repository.save(managerGoal);
    }

    public void updateManagerGoal(Long id, ManagerGoalCreateUpdatePayloadV2 payload) {
        ManagerGoalV2 managerGoal = managerGoalV2Repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Manager goal not found with ID: " + id));

        managerGoal.setStartDate(payload.getStartDate());
        managerGoal.setEndDate(payload.getEndDate());
        managerGoal.setStatus(payload.getGoalStatus() != null ? payload.getGoalStatus() : managerGoal.getStatus());

        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            for (MedicineQuotePayloadV2 quotePayload : payload.getMedicineQuotes()) {
                Medicine medicine = medicineRepository.findById(quotePayload.getMedicineId())
                        .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId()));
                Optional<MedicineQuoteV2> existingQuote = managerGoal.getMedicineQuoteV2s().stream()
                        .filter(q -> q.getMedicine().getId().equals(quotePayload.getMedicineId()))
                        .findFirst();
                if (existingQuote.isPresent()) {
                    existingQuote.get().setQuote(quotePayload.getQuote());
                } else {
                    MedicineQuoteV2 newQuote = new MedicineQuoteV2();
                    newQuote.setMedicine(medicine);
                    newQuote.setQuote(quotePayload.getQuote());
                    newQuote.setManagerGoalV2(managerGoal);
                    managerGoal.getMedicineQuoteV2s().add(newQuote);
                }
            }
        }

        if (payload.getFieldEnvQuotes() != null && !payload.getFieldEnvQuotes().isEmpty()) {
            for (FieldEnvQuotePayloadV2 fieldPayload : payload.getFieldEnvQuotes()) {
                Optional<FieldEnvQuoteV2> existingField = managerGoal.getFieldEnvQuoteV2s().stream()
                        .filter(f -> f.getField() == fieldPayload.getField())
                        .findFirst();
                if (existingField.isPresent()) {
                    existingField.get().setQuote(fieldPayload.getFieldQuote());
                } else {
                    FieldEnvQuoteV2 newField = new FieldEnvQuoteV2();
                    newField.setField(fieldPayload.getField());
                    newField.setQuote(fieldPayload.getFieldQuote());
                    newField.setManagerGoalV2(managerGoal);
                    managerGoal.getFieldEnvQuoteV2s().add(newField);
                }
            }
        }

        if (payload.getMedAgenEnvQuotes() != null && !payload.getMedAgenEnvQuotes().isEmpty()) {
            for (MedAgentQuoteEnvPayloadV2 agentPayload : payload.getMedAgenEnvQuotes()) {
                District district = districtRepository.findById(agentPayload.getDistrictId())
                        .orElseThrow(() -> new NotFoundException("District not found with ID: " + agentPayload.getDistrictId()));
                Optional<MedAgentEnvV2> existingEnv = managerGoal.getMedAgentEnvs().stream()
                        .filter(e -> e.getDistrict().getId().equals(agentPayload.getDistrictId()))
                        .findFirst();
                if (existingEnv.isPresent()) {
                    existingEnv.get().setQuote(agentPayload.getQuote());
                } else {
                    MedAgentEnvV2 newEnv = new MedAgentEnvV2();
                    newEnv.setDistrict(district);
                    newEnv.setQuote(agentPayload.getQuote());
                    newEnv.setManagerGoalV2(managerGoal);
                    managerGoal.getMedAgentEnvs().add(newEnv);
                }
            }
        }

        managerGoalV2Repository.save(managerGoal);
    }

    @Transactional
    public boolean deleteManagerGoal(Long id) {
        ManagerGoalV2 managerGoal = managerGoalV2Repository.findById(id)
                .orElse(null);

        if (managerGoal != null) {
            if (managerGoal.getMedicineQuoteV2s() != null) {
                managerGoal.getMedicineQuoteV2s().clear();
            }

            if (managerGoal.getFieldEnvQuoteV2s() != null) {
                managerGoal.getFieldEnvQuoteV2s().clear();
            }

            if (managerGoal.getMedAgentEnvs() != null) {
                managerGoal.getMedAgentEnvs().clear();
            }

            managerGoalV2Repository.delete(managerGoal);
            return true;
        }
        return false;
    }


    // --- Private helper methods now accepting YearMonth ---

    private SalesQuoteDTO getSalesQuoteDTO(ManagerGoalV2 managerGoal, YearMonth targetMonth) { // Added targetMonth
        if (managerGoal == null) {
            return null;
        }
        UUID managerId = managerGoal.getManagerId().getUserId();
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found with ID: " + managerId));
        Long regionId = manager.getDistrict().getRegion().getId();
        if (regionId == null) {
            throw new IllegalStateException("Region ID is null for manager ID: " + managerId);
        }

        long totalSales = doctorContractV2Repository.findByRegion(regionId).stream()
                .flatMap(contract -> contract.getMedicineWithQuantityDoctorV2s().stream())
                .flatMap(medicine -> medicine.getContractMedicineDoctorAmountV2s().stream())
                .filter(amount -> amount.getYearMonth().equals(targetMonth)) // Use targetMonth
                .mapToLong(ContractMedicineDoctorAmountV2::getAmount)
                .sum();

        long targetSales = managerGoal.getMedicineQuoteV2s().stream()
                .mapToLong(MedicineQuoteV2::getQuote)
                .sum();

        return new SalesQuoteDTO(totalSales, targetSales);
    }

    private ManagerProfileKPIDTOV2 getManagerProfileKPIDTOV2(UUID managerId, YearMonth targetMonth) { // Added targetMonth
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found with ID: " + managerId));
        Long regionId = manager.getDistrict().getRegion().getId();

        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay(); // Use targetMonth
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(23, 59, 59); // Use targetMonth

        long doctorsInDatabase = userRepository.findByRoleAndRegionId(regionId, Role.DOCTOR);
        long totalPrescriptions = recipeRepository.countByRegionId(regionId);
        long prescriptionsNewDoctors = recipeRepository.countByRegionIdAndDoctorIdsAndMonth(regionId, startDate, endDate);
        long totalWorking = userRepository.findByRoleAndRegionId(regionId, Role.MEDAGENT);
        long totalMedicines = recipeRepository.countMedicationsByDoctorIds(regionId);
        long medicineNewDoctors = recipeRepository.countMedicationsByDoctorIdsAndMonth(regionId, startDate, endDate);
        long newDoctorsThisMonth = userRepository.findCreatedDoctorsThisMonthByRegionAndMonth(regionId, null, startDate, endDate).size();
        long coveredDistricts = 0;
        long coveredWorkPlaces = 0;

        return new ManagerProfileKPIDTOV2(
                managerId,
                doctorsInDatabase,
                totalPrescriptions,
                prescriptionsNewDoctors,
                totalWorking,
                totalMedicines,
                medicineNewDoctors,
                newDoctorsThisMonth,
                coveredDistricts,
                coveredWorkPlaces
        );
    }

    private ManagerGoalDTOV2 convertToManagerGoalDTOV2(ManagerGoalV2 managerGoal, YearMonth targetMonth) { // Added targetMonth
        if (managerGoal == null) {
            return null;
        }
        ManagerGoalDTOV2 dto = new ManagerGoalDTOV2();
        dto.setId(managerGoal.getGoalId());
        dto.setManagerId(managerGoal.getManagerId().getUserId());
        dto.setCreatedAt(managerGoal.getCreatedAt());
        dto.setStartDate(managerGoal.getStartDate());
        dto.setEndDate(managerGoal.getEndDate());
        dto.setStatus(managerGoal.getStatus());

        // Use targetMonth instead of YearMonth.now()
        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(23, 59, 59);
        UUID managerId = managerGoal.getManagerId().getUserId();
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found with ID: " + managerId));
        Long regionId = manager.getDistrict().getRegion().getId();
        if (regionId == null) {
            throw new IllegalStateException("Region ID is null for manager ID: " + managerId);
        }

        if (managerGoal.getMedicineQuoteV2s() != null) {
            List<MedicineQuoteDTOV2> medicineQuoteDTOs = managerGoal.getMedicineQuoteV2s().stream().map(quote -> {
                MedicineQuoteDTOV2 quoteDTO = new MedicineQuoteDTOV2();
                quoteDTO.setId(quote.getId());
                quoteDTO.setMedicine(quote.getMedicine());
                quoteDTO.setQuote(quote.getQuote());

                Long amount = contractMedicineDoctorAmountV2Repository.sumMedicineAmountsByRegionMedicineAndMonth(
                        regionId,
                        quote.getMedicine().getId(),
                        targetMonth // Use targetMonth
                ).orElse(0L);

                quoteDTO.setAmount(amount);
                quoteDTO.setYearMonth(targetMonth); // Use targetMonth
                return quoteDTO;
            }).collect(Collectors.toList());
            dto.setMedicineQuoteDTOV2List(medicineQuoteDTOs);
        }

        if (managerGoal.getFieldEnvQuoteV2s() != null) {
            List<FieldEnvDTOV2> fieldEnvDTOs = managerGoal.getFieldEnvQuoteV2s().stream().map(fieldEnv -> {
                FieldEnvDTOV2 fieldDTO = new FieldEnvDTOV2();
                fieldDTO.setId(fieldEnv.getId());
                fieldDTO.setField(fieldEnv.getField());
                fieldDTO.setQuote(fieldEnv.getQuote());
                Long amount = (long) userRepository.findCreatedDoctorsThisMonthByRegionAndMonth(
                        regionId,
                        fieldDTO.getField(),
                        startDate, // derived from targetMonth
                        endDate    // derived from targetMonth
                ).size();
                fieldDTO.setAmount(amount);
                fieldDTO.setYearMonth(targetMonth); // Use targetMonth
                return fieldDTO;
            }).collect(Collectors.toList());
            dto.setFieldEnvDTOV2List(fieldEnvDTOs);
        }

        if (managerGoal.getMedAgentEnvs() != null) {
            List<MedAgentEnvDTOV2> medAgentEnvDTOs = managerGoal.getMedAgentEnvs().stream().map(agentEnv -> {
                MedAgentEnvDTOV2 agentDTO = new MedAgentEnvDTOV2();
                agentDTO.setId(agentEnv.getId());
                agentDTO.setQuote(agentEnv.getQuote());
                agentDTO.setDistrict(new DistrictDTO(
                        agentEnv.getDistrict().getId(),
                        agentEnv.getDistrict().getName(),
                        agentEnv.getDistrict().getNameUzCyrillic(),
                        agentEnv.getDistrict().getNameUzLatin(),
                        agentEnv.getDistrict().getNameRussian(),
                        agentEnv.getDistrict().getRegion().getId()
                ));
                Long amount = (long) userRepository.findMedicalAgentsByDistrictAndMonth(
                        agentEnv.getDistrict().getId(),
                        startDate, // derived from targetMonth
                        endDate    // derived from targetMonth
                ).size();
                agentDTO.setAmount(amount);
                agentDTO.setYearMonth(targetMonth); // Use targetMonth
                return agentDTO;
            }).collect(Collectors.toList());
            dto.setMedAgentEnvDTOV2List(medAgentEnvDTOs);
        }

        return dto;
    }
}