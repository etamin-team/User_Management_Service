package com.example.user_management_service.service;

import com.example.user_management_service.exception.*;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.model.v2.ContractMedicineDoctorAmountV2;
import com.example.user_management_service.model.v2.DoctorContractV2;
import com.example.user_management_service.model.v2.dto.ContractDTOV2;
import com.example.user_management_service.model.v2.dto.MedicineQuoteDTOV2;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.repository.v2.DoctorContractV2Repository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ContractService {


    private final RecipeRepository recipeRepository;
    private final DistrictRegionService districtRegionService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final DoctorContractV2Repository doctorContractV2Repository;
    // Removed: private final ContractRepository contractRepository; // No longer needed as V1 contracts are phased out

    // This method is for V2 contracts (DoctorContractV2)
    private ContractDTOV2 mapToContractDTOV2(DoctorContractV2 contract, YearMonth targetMonth) {
        List<MedicineQuoteDTOV2> medicineQuotes = contract.getMedicineWithQuantityDoctorV2s().stream()
                .map(m -> {
                    // Optimized: Use a single stream filter and then map for both amount and correction
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

    public Page<ContractDTOV2> getContractsByStatus(List<Long> regionIds, GoalStatus goalStatus,YearMonth targetMonth, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DoctorContractV2> contracts = doctorContractV2Repository.findByStatus(regionIds, goalStatus, pageable);
        return contracts.map(contract -> mapToContractDTOV2(contract, targetMonth));
    }


    public DoctorRecipeStatsDTO getDoctorRecipeStatsDTOByDoctorId(UUID doctorId) {
        DoctorRecipeStatsDTO doctorRecipeStatsDTO = new DoctorRecipeStatsDTO();
        doctorRecipeStatsDTO.setDoctorId(doctorId);
        doctorRecipeStatsDTO.setRecipesCreatedThisMonth(recipeRepository.countRecipesCreatedThisMonthByDoctor(doctorId));
        doctorRecipeStatsDTO.setAverageRecipesPerMonth(recipeRepository.averageRecipesLast12MonthsByDoctor(doctorId));
        return doctorRecipeStatsDTO;
    }

    // --- Renamed and Updated: This is the V2 equivalent of getContractsByMedAgent ---
    public Page<ContractDTOV2> getContractsByMedAgentV2(
            UUID medAgentId,
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
        // Use the V2 repository method
        Page<DoctorContractV2> contracts = doctorContractV2Repository.findAllContractsByAgent(
                medAgentId, regionId, districtId, workPlaceId,
                firstName, lastName, middleName, fieldName, pageable);
        return contracts.map(contract -> mapToContractDTOV2(contract, targetMonth));
    }


    // --- V2 Method: getFilteredContracts (no regionIds list overload) ---
    public Page<ContractDTOV2> getFilteredContractsV2(
            Long regionId,
            Long districtId,
            Long workPlaceId,
            String nameQuery, // This nameQuery handles first, last, middle name
            Field fieldName,
            LocalDate startDate,
            LocalDate endDate,
            Long medicineId,
            int page,
            int size,
            YearMonth targetMonth // New parameter for V2 mapping
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Use the new V2 repository method
        Page<DoctorContractV2> contractPage = doctorContractV2Repository.findFilteredContracts(
                regionId, districtId, workPlaceId, nameQuery, fieldName, startDate, endDate, medicineId, pageable
        );

        // Convert each DoctorContractV2 entity to ContractDTOV2 and maintain pagination
        return contractPage.map(contract -> mapToContractDTOV2(contract, targetMonth));
    }

    // --- V2 Method: getFilteredContracts (with List<Long> regionIds overload) ---
    public Page<ContractDTOV2> getFilteredContractsV2(
            List<Long> regionIds,
            Long regionId,
            Long districtId,
            Long workPlaceId,
            String nameQuery, // This nameQuery handles first, last, middle name
            Field fieldName,
            LocalDate startDate,
            LocalDate endDate,
            Long medicineId,
            int page,
            int size,
            YearMonth targetMonth // New parameter for V2 mapping
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Use the new V2 repository method
        Page<DoctorContractV2> contractPage = doctorContractV2Repository.findFilteredContracts(
                regionIds, regionId, districtId, workPlaceId, nameQuery, fieldName, startDate, endDate, medicineId, pageable
        );

        // Convert each DoctorContractV2 entity to ContractDTOV2 and maintain pagination
        return contractPage.map(contract -> mapToContractDTOV2(contract, targetMonth));
    }

    private String[] prepareNameParts(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return new String[0];
        }

        String[] nameParts = nameQuery.split(" ");
        List<String> cleanParts = new ArrayList<>();
        for (String part : nameParts) {
            if (part != null && !part.trim().isEmpty()) {
                cleanParts.add(part.trim());
            }
        }
        return cleanParts.toArray(new String[0]);
    }

    public List<LineChart> getDoctorRecipeChart(UUID doctorId, LocalDate startDate, LocalDate endDate,
                                                int numberOfParts) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0 || numberOfParts <= 0) {
            throw new ChartException("Invalid date range or part count");
        }

        long interval = totalDays / numberOfParts;
        List<LineChart> chart = new ArrayList<>();

        for (int i = 0; i < numberOfParts; i++) {
            LocalDate from = startDate.plusDays(i * interval);
            LocalDate to = (i == numberOfParts - 1) ? endDate : from.plusDays(interval - 1);

            Long totalPrice = recipeRepository.getTotalPriceBetweenDatesAndDoctor(doctorId, from, to);
            if (totalPrice == null) totalPrice = 0L;

            chart.add(new LineChart(from, to, totalPrice));
        }

        return chart;
    }

    public void editStatusContract(Long id, GoalStatus goalStatus) {
        // Now updates DoctorContractV2
        DoctorContractV2 contract = doctorContractV2Repository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException("Contract not found with id: " + id));
        contract.setStatus(goalStatus);
        doctorContractV2Repository.save(contract);
    }
}