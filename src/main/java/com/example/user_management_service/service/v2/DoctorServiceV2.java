package com.example.user_management_service.service.v2;

import com.example.user_management_service.exception.ContractNotFoundException;
import com.example.user_management_service.exception.DoctorContractException;
import com.example.user_management_service.exception.DoctorContractExistsException;
import com.example.user_management_service.exception.NotFoundException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.OutOfContractMedicineAmountDTO;
import com.example.user_management_service.model.dto.PreparationDto;
import com.example.user_management_service.model.dto.RecipeDto;
import com.example.user_management_service.model.v2.*;
import com.example.user_management_service.model.v2.dto.ContractDTOV2;
import com.example.user_management_service.model.v2.dto.DoctorProfileDTOV2;
import com.example.user_management_service.model.v2.dto.MedicineQuoteDTOV2;
import com.example.user_management_service.model.v2.payload.DoctorContractCreateUpdatePayloadV2;
import com.example.user_management_service.model.v2.payload.MedicineQuotePayloadV2;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RecipeRepository;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.v2.DoctorContractV2Repository;
import com.example.user_management_service.repository.v2.DoctorContractVisibilityRepository;
import com.example.user_management_service.repository.v2.OutOfContractMedicineAmountV2Repository;
import com.example.user_management_service.role.Role;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date-9/11/2025
 * By Sardor Tokhirov
 * Time-7:47 AM (GMT+5)
 */
@Service
@AllArgsConstructor
@Transactional
public class DoctorServiceV2 {

    private final DoctorContractV2Repository doctorContractV2Repository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final RecipeRepository recipeRepository;
    private final OutOfContractMedicineAmountV2Repository outOfContractMedicineAmountV2Repository;
    private final DoctorContractVisibilityRepository visibilityRepository;

    // --- Methods for creating/updating/deleting contracts and saving recipes (no YearMonth filter needed here) ---
    public void createContractByManager(DoctorContractCreateUpdatePayloadV2 payload) {
        if (doctorContractV2Repository.findByDoctorUserId(payload.getDoctorId()).isPresent()) {
            throw new DoctorContractExistsException("Doctor already has an active contract");
        }

        DoctorContractV2 contract = new DoctorContractV2();
        contract.setDoctor(userRepository.findById(payload.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + payload.getDoctorId())));
        contract.setCreatedBy(userRepository.findById(payload.getCreatorId())
                .orElseThrow(() -> new NotFoundException("Creator not found with ID: " + payload.getCreatorId())));
        contract.setCreatedAt(LocalDate.now());
        contract.setStartDate(payload.getStartDate());
        contract.setEndDate(payload.getEndDate());
        contract.setStatus(GoalStatus.APPROVED);
        contract.setContractType(payload.getContractType());

        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            List<MedicineWithQuantityDoctorV2> medicineQuotes = payload.getMedicineQuotes().stream().map(quotePayload -> {
                MedicineWithQuantityDoctorV2 medicineQuote = new MedicineWithQuantityDoctorV2();
                medicineQuote.setMedicine(medicineRepository.findById(quotePayload.getMedicineId())
                        .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId())));
                medicineQuote.setQuote(quotePayload.getQuote());
                medicineQuote.setDoctorContract(contract);
                return medicineQuote;
            }).collect(Collectors.toList());
            contract.setMedicineWithQuantityDoctorV2s(medicineQuotes);
        }

        doctorContractV2Repository.save(contract);
    }

    public void createContractByMedAgent(DoctorContractCreateUpdatePayloadV2 payload) {
        if (doctorContractV2Repository.findByDoctorUserId(payload.getDoctorId()).isPresent()) {
            throw new DoctorContractExistsException("Doctor already has an active contract");
        }

        DoctorContractV2 contract = new DoctorContractV2();
        contract.setDoctor(userRepository.findById(payload.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + payload.getDoctorId())));
        contract.setCreatedBy(userRepository.findById(payload.getCreatorId())
                .orElseThrow(() -> new NotFoundException("Creator not found with ID: " + payload.getCreatorId())));
        contract.setCreatedAt(LocalDate.now());
        contract.setStartDate(payload.getStartDate());
        contract.setEndDate(payload.getEndDate());
        contract.setStatus(GoalStatus.PENDING_REVIEW);
        contract.setContractType(payload.getContractType());

        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
            List<MedicineWithQuantityDoctorV2> medicineQuotes = payload.getMedicineQuotes().stream().map(quotePayload -> {
                MedicineWithQuantityDoctorV2 medicineQuote = new MedicineWithQuantityDoctorV2();
                medicineQuote.setMedicine(medicineRepository.findById(quotePayload.getMedicineId())
                        .orElseThrow(() -> new NotFoundException("Medicine not found with ID: " + quotePayload.getMedicineId())));
                medicineQuote.setQuote(quotePayload.getQuote());
                medicineQuote.setDoctorContract(contract);
                return medicineQuote;
            }).collect(Collectors.toList());
            contract.setMedicineWithQuantityDoctorV2s(medicineQuotes);
        }

        doctorContractV2Repository.save(contract);
    }

    public boolean deleteContract(Long contractId) {
        if (doctorContractV2Repository.existsById(contractId)) {
            doctorContractV2Repository.deleteById(contractId);
            return true;
        }
        return false;
    }

    public void updateContract(Long contractId, DoctorContractCreateUpdatePayloadV2 payload) {
        DoctorContractV2 contract = doctorContractV2Repository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found with ID: " + contractId));

        contract.setStartDate(payload.getStartDate());
        contract.setEndDate(payload.getEndDate());
        contract.setContractType(payload.getContractType());

        if (payload.getMedicineQuotes() != null && !payload.getMedicineQuotes().isEmpty()) {
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
        doctorContractV2Repository.save(contract);
    }

    public void saveRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setFirstName(recipeDto.getFirstName());
        recipe.setLastName(recipeDto.getLastName());
        recipe.setDateOfBirth(recipeDto.getDateOfBirth());
        recipe.setPhoneNumber(recipeDto.getPhoneNumber());
        recipe.setPhoneNumberPrefix(recipeDto.getPhoneNumberPrefix());
        recipe.setDiagnosis(recipeDto.getDiagnosis());
        recipe.setComment(recipeDto.getComment());

        List<Preparation> preparations = recipeDto.getPreparations().stream()
                .map(this::mapPreparationDtoToEntity)
                .collect(Collectors.toList());

        recipe.setPreparations(preparations);
        recipe.setDateCreation(LocalDate.now());
        User doctor = userRepository.findById(recipeDto.getDoctorId())
                .orElseThrow(() -> new DoctorContractException("Doctor not found with ID: " + recipeDto.getDoctorId()));
        recipe.setDoctorId(doctor);
        ContractType contractType = doctorContractV2Repository.findByDoctorUserId(doctor.getUserId())
                .map(DoctorContractV2::getContractType)
                .orElse(ContractType.RECIPE);
        recipe.setContractType(contractType);
        recipeRepository.save(recipe);

        if (preparations != null && !preparations.isEmpty()) {
            List<Long> medicineIds = preparations.stream()
                    .map(Preparation::getMedicine)
                    .filter(Objects::nonNull)
                    .map(Medicine::getId)
                    .collect(Collectors.toList());
            saveContractMedicineAmount(recipe.getDoctorId().getUserId(), medicineIds);
        }
    }

    private Preparation mapPreparationDtoToEntity(PreparationDto preparationDto) {
        if (preparationDto == null) {
            return null;
        }
        Preparation preparation = new Preparation();
        preparation.setName(preparationDto.getName());
        preparation.setAmount(preparationDto.getAmount());
        preparation.setQuantity(preparationDto.getQuantity());
        preparation.setTimesInDay(preparationDto.getTimesInDay());
        preparation.setDays(preparationDto.getDays());
        preparation.setType(preparationDto.getType());

        Medicine medicine = medicineRepository.findById(preparationDto.getMedicineId())
                .orElseThrow(() -> new DoctorContractException("Medicine with ID " + preparationDto.getMedicineId() + " not found"));
        preparation.setMedicine(medicine);

        return preparation;
    }

    @Transactional
    public void saveContractMedicineAmount(UUID doctorId, List<Long> medicineIds) {
        // This method always records for the current month.
        YearMonth currentMonth = YearMonth.now();
        DoctorContractV2 contract = doctorContractV2Repository.findByDoctorUserId(doctorId).orElse(null);
        List<OutOfContractMedicineAmountV2> outOfContractMedicines = outOfContractMedicineAmountV2Repository
                .findAllForDoctorThisMonth(doctorId, currentMonth).orElse(new ArrayList<>());

        if (contract == null || contract.getMedicineWithQuantityDoctorV2s().isEmpty()) {
            handleOutOfContractMedicines(doctorId, medicineIds, outOfContractMedicines, currentMonth);
            return;
        }

        Map<Long, MedicineWithQuantityDoctorV2> medicineMap = contract.getMedicineWithQuantityDoctorV2s().stream()
                .collect(Collectors.toMap(m -> m.getMedicine().getId(), m -> m));

        for (Long medicineId : medicineIds) {
            MedicineWithQuantityDoctorV2 medicineEntry = medicineMap.get(medicineId);

            if (medicineEntry != null) {
                updateContractMedicineAmounts(medicineEntry, currentMonth);
                // No need to save the whole contract here; updates within collections are usually managed by JPA
                // if the parent entity (contract) is managed.
            } else {
                updateOrCreateOutOfContractMedicine(doctorId, medicineId, outOfContractMedicines, contract, currentMonth);
            }
        }
    }

    @Transactional
    public void updateContractMedicineAmounts(MedicineWithQuantityDoctorV2 medicineEntry, YearMonth currentMonth) {
        List<ContractMedicineDoctorAmountV2> amounts = medicineEntry.getContractMedicineDoctorAmountV2s();
        Optional<ContractMedicineDoctorAmountV2> existingAmount = amounts.stream()
                .filter(a -> a.getYearMonth().equals(currentMonth))
                .findFirst();

        if (existingAmount.isPresent()) {
            ContractMedicineDoctorAmountV2 amount = existingAmount.get();
            amount.setAmount(amount.getAmount() + 1);
            amount.setCorrection(amount.getCorrection() + 1);
        } else {
            ContractMedicineDoctorAmountV2 newAmount = new ContractMedicineDoctorAmountV2();
            newAmount.setAmount(1L);
            newAmount.setCorrection(1L);
            newAmount.setYearMonth(currentMonth);
            newAmount.setMedicineWithQuantityDoctor(medicineEntry);
            amounts.add(newAmount);
        }
    }

    private void handleOutOfContractMedicines(UUID doctorId, List<Long> medicineIds,
                                              List<OutOfContractMedicineAmountV2> outOfContractMedicines, YearMonth currentMonth) {
        for (Long medicineId : medicineIds) {
            updateOrCreateOutOfContractMedicine(doctorId, medicineId, outOfContractMedicines, null, currentMonth);
        }
    }

    private void updateOrCreateOutOfContractMedicine(UUID doctorId, Long medicineId,
                                                     List<OutOfContractMedicineAmountV2> outOfContractMedicines,
                                                     DoctorContractV2 contract, YearMonth currentMonth) {
        Optional<OutOfContractMedicineAmountV2> existingMedicine = outOfContractMedicines.stream()
                .filter(m -> m.getMedicine().getId().equals(medicineId) && m.getYearMonth().equals(currentMonth))
                .findFirst();

        if (existingMedicine.isPresent()) {
            OutOfContractMedicineAmountV2 outOfContractMedicine = existingMedicine.get();
            outOfContractMedicine.setAmount(outOfContractMedicine.getAmount() + 1);
            outOfContractMedicineAmountV2Repository.save(outOfContractMedicine);
        } else {
            Medicine medicine = medicineRepository.findById(medicineId)
                    .orElseThrow(() -> new DoctorContractException("Medicine not found with ID: " + medicineId));

            OutOfContractMedicineAmountV2 newOutOfContractMedicine = new OutOfContractMedicineAmountV2();
            newOutOfContractMedicine.setAmount(1L);
            newOutOfContractMedicine.setYearMonth(currentMonth);
            newOutOfContractMedicine.setDoctor(contract != null ? contract.getDoctor() :
                    userRepository.findById(doctorId)
                            .orElseThrow(() -> new ContractNotFoundException("Doctor not found with ID: " + doctorId)));
            newOutOfContractMedicine.setMedicine(medicine);

            outOfContractMedicineAmountV2Repository.save(newOutOfContractMedicine);
        }
    }

    // --- Public Methods for fetching contract/profile DTOs (YearMonth filter applied) ---

    public ContractDTOV2 getContractById(Long contractId, YearMonth targetMonth) {
        DoctorContractV2 contract = doctorContractV2Repository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found with ID: " + contractId));

        return mapToContractDTOV2(contract, targetMonth);
    }

    public ContractDTOV2 getContractByDoctorId(UUID doctorId, YearMonth targetMonth) {
        // This method applies visibility filter
        boolean isVisible = getContractVisibility(doctorId);
        if (!isVisible) {
            return null; // Contract is not visible, return null
        }
        return doctorContractV2Repository.findByDoctorUserId(doctorId)
                .map(contract -> mapToContractDTOV2(contract, targetMonth))
                .orElse(null);
    }

    /**
     * Retrieves a doctor's contract DTO by doctor ID, explicitly ignoring the visibility status.
     * This is suitable for internal use or manager-level views.
     *
     * @param doctorId The ID of the doctor.
     * @param targetMonth The month for which to retrieve medicine amounts.
     * @return The ContractDTOV2, or null if no contract is found.
     */
    public ContractDTOV2 getContractByDoctorIdForManager(UUID doctorId, YearMonth targetMonth) {
        // This method intentionally bypasses the visibility check.
        return doctorContractV2Repository.findByDoctorUserId(doctorId)
                .map(contract -> mapToContractDTOV2(contract, targetMonth))
                .orElse(null);
    }

    public DoctorProfileDTOV2 getDoctorProfileByDoctorId(UUID doctorId, YearMonth targetMonth) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        // ContractDTO should respect visibility for a general doctor profile view
        ContractDTOV2 contractDTO = getContractByDoctorId(doctorId, targetMonth);

        List<OutOfContractMedicineAmountV2> outOfContractMedicines = outOfContractMedicineAmountV2Repository
                .findAllForDoctorThisMonth(doctorId, targetMonth)
                .orElse(new ArrayList<>());

        List<OutOfContractMedicineAmountDTO> outOfContractDTOs = outOfContractMedicines.stream()
                .map(o -> new OutOfContractMedicineAmountDTO(o.getId(), o.getAmount(), o.getMedicine()))
                .collect(Collectors.toList());

        return new DoctorProfileDTOV2(doctorId, contractDTO, outOfContractDTOs);
    }

    /**
     * Retrieves a doctor's complete profile DTO by doctor ID, explicitly ignoring the contract visibility status.
     * This is suitable for manager-level views.
     *
     * @param doctorId The ID of the doctor.
     * @param targetMonth The month for which to retrieve medicine amounts.
     * @return The DoctorProfileDTOV2.
     * @throws NotFoundException if the doctor is not found.
     */
    public DoctorProfileDTOV2 getDoctorProfileByDoctorIdForManager(UUID doctorId, YearMonth targetMonth) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        // For a manager view, the contract should be retrieved regardless of visibility
        ContractDTOV2 contractDTO = getContractByDoctorIdForManager(doctorId, targetMonth); // Uses the method that bypasses visibility

        List<OutOfContractMedicineAmountV2> outOfContractMedicines = outOfContractMedicineAmountV2Repository
                .findAllForDoctorThisMonth(doctorId, targetMonth)
                .orElse(new ArrayList<>());

        List<OutOfContractMedicineAmountDTO> outOfContractDTOs = outOfContractMedicines.stream()
                .map(o -> new OutOfContractMedicineAmountDTO(o.getId(), o.getAmount(), o.getMedicine()))
                .collect(Collectors.toList());

        return new DoctorProfileDTOV2(doctorId, contractDTO, outOfContractDTOs);
    }

    // --- Private Helper method to map DoctorContractV2 to ContractDTOV2 (YearMonth filter applied) ---
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

    // --- Contract Visibility Management ---
    public void setContractVisibility(UUID doctorId, boolean isContractVisible) {
        DoctorContractVisibility visibility = visibilityRepository.findByDoctorId(doctorId);
        if (visibility == null) {
            visibility = new DoctorContractVisibility();
            visibility.setDoctorId(doctorId);
        }
        visibility.setContractVisible(isContractVisible);
        visibilityRepository.save(visibility);
    }

    public void setAllContractsVisibility(boolean isContractVisible) {
        List<User> doctors = userRepository.findDoctors();
        for (User doctor : doctors) {
            DoctorContractVisibility visibility = visibilityRepository.findByDoctorId(doctor.getUserId());
            if (visibility == null) {
                visibility = new DoctorContractVisibility();
                visibility.setDoctorId(doctor.getUserId());
            }
            visibility.setContractVisible(isContractVisible);
            visibilityRepository.save(visibility);
        }
    }

    public boolean getContractVisibility(UUID doctorId) {
        DoctorContractVisibility visibility = visibilityRepository.findByDoctorId(doctorId);
        return visibility != null ? visibility.isContractVisible() : true; // Default to true if no explicit visibility record
    }

    // --- Contract Status Management and Paginated Retrieval ---
    public void enableContract(Long id) {
        DoctorContractV2 contract = doctorContractV2Repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));

        if (contract.getStatus() == GoalStatus.APPROVED) {
            throw new IllegalStateException("Contract is already approved.");
        }

        contract.setStatus(GoalStatus.APPROVED);
        doctorContractV2Repository.save(contract);
    }

    public Page<ContractDTOV2> getContractsByStatus(GoalStatus goalStatus, int page, int size, YearMonth targetMonth) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DoctorContractV2> contracts = doctorContractV2Repository.findByStatus(goalStatus, pageable);
        // Map each contract in the page using the targetMonth
        return contracts.map(contract -> mapToContractDTOV2(contract, targetMonth));
    }

    public void editStatusContract(Long id, GoalStatus goalStatus) {
        DoctorContractV2 contract = doctorContractV2Repository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException("Contract not found with id: " + id));
        contract.setStatus(goalStatus);
        doctorContractV2Repository.save(contract);
    }

    public void declineContract(Long id) {
        DoctorContractV2 contract = doctorContractV2Repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));

        if (contract.getStatus() == GoalStatus.DECLINED) {
            throw new DoctorContractException("Contract is already declined.");
        }

        contract.setStatus(GoalStatus.DECLINED);
        doctorContractV2Repository.save(contract);
    }
    public boolean getMostDoctorsContractVisibility() {
        long totalDoctors = userRepository.countByRole(Role.DOCTOR);

        if (totalDoctors == 0) {
            return false; // No doctors, so no majority can be 'on'.
        }

        // Count doctors whose visibility is explicitly set to true
        long explicitlyVisibleCount = visibilityRepository.countVisibleContracts();

        // Count doctors whose visibility is explicitly set to false
        long explicitlyNotVisibleCount = visibilityRepository.countNotVisibleContracts();

        // Calculate the number of doctors who DO NOT have an entry in DoctorContractVisibility
        // These doctors are considered visible by default (as per your getContractVisibility logic)
        long doctorsWithoutVisibilityRecord = totalDoctors - (explicitlyVisibleCount + explicitlyNotVisibleCount);

        // Calculate the total count of doctors considered 'on' (visible)
        long totalOnCount = explicitlyVisibleCount + doctorsWithoutVisibilityRecord;

        // Calculate the total count of doctors considered 'off' (not visible)
        long totalOffCount = explicitlyNotVisibleCount;

        // Return true if the 'on' count is greater than the 'off' count
        return totalOnCount > totalOffCount;
    }

}