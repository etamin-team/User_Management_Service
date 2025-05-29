package com.example.user_management_service.service;

import com.example.user_management_service.exception.*;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ContractService {


    private final ContractRepository contractRepository;
    private final FieldWithQuantityRepository fieldWithQuantityRepository;
    private final OutOfContractMedicineAmountRepository outOfContractMedicineAmountRepository;
    private final MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;
    private final RecipeRepository recipeRepository;
    private final DistrictRegionService districtRegionService;
    private final ContractDistrictAmountRepository contractDistrictAmountRepository;
    private final ManagerGoalRepository managerGoalRepository;
    private final UserService userService;
    private final ContractMedicineManagerAmountRepository contractMedicineManagerAmountRepository;
    private final ContractMedicineMedAgentAmountRepository contractMedicineMedAgentAmountRepository;
    private MedicineAgentGoalQuantityRepository medicineAgentGoalQuantityRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private AgentGoalRepository agentGoalRepository;
    private final ContractMedicineDoctorAmountRepository contractMedicineDoctorAmountRepository;
    private final MedicineManagerGoalQuantityRepository medicineManagerGoalQuantityRepository;
    private final ContractFieldAmountRepository contractFieldAmountRepository;
    private final FieldGoalQuantityRepository fieldGoalQuantityRepository;


    // Doctor Contract

    public ContractDTO managerCreateContract(ContractDTO contractDTO) {
        if (contractRepository.findActiveOrPendingContractByDoctorId(contractDTO.getDoctorId()).isPresent()) {
            throw new DoctorContractExistsException("Doctor had already assigned contract doctorId:" + contractDTO.getDoctorId());
        }
        ManagerGoal managerGoal = managerGoalRepository.getGoalsByManagerId(contractDTO.getManagerId()).orElse(null);
        System.out.println("Doctor: 11111111111111111111111111111111111111111111111111111111111111111");
        User doctor = userRepository.findById(contractDTO.getDoctorId())
                .orElseThrow(() -> new DoctorContractException("Doctor not found"));
        List<DistrictGoalQuantity> districtGoalQuantities = managerGoal != null ? managerGoal.getDistrictGoalQuantities() : null;
        System.out.println("District: 222222222222222222222222222222222222222222222222222222222222222222222");
        boolean isDistrictIdMatches =districtGoalQuantities!=null && districtGoalQuantities
                .stream()
                .map(dq -> dq.getDistrict().getId())
                .anyMatch(id -> id.equals(doctor.getDistrict().getId()));
        System.out.println("Boolean: 444444444444444444444444444444444444444444444444444444444444444444444");
        if (managerGoal != null && managerGoal.getDistrictGoalQuantities() != null && managerGoal.getDistrictGoalQuantities().size() > 0 && !isDistrictIdMatches) {

//            throw new DoctorContractException("DistrictId of Agent Contract   doesn't match with Doctors districtId");
            ;
            DistrictGoalQuantity districtGoalQuantity = districtGoalQuantities
                    .stream()
                    .filter(dq ->
                            dq.getDistrict().getId().equals(doctor.getDistrict().getId())
                    ).findAny().orElse(null);

            if (districtGoalQuantity != null) {
                ContractDistrictAmount contractDistrictAmount = districtGoalQuantity.getContractDistrictAmount();
                contractDistrictAmount.setAmount(contractDistrictAmount.getAmount() + 1);
                contractDistrictAmountRepository.save(contractDistrictAmount);
            }

        }
        List<FieldGoalQuantity> fieldGoalQuantities = managerGoal != null ? managerGoal.getFieldGoalQuantities() : null;
        if (fieldGoalQuantities == null || fieldGoalQuantities.isEmpty()) {
//            throw new DoctorContractException("No fields found in ManagerGoal.");
        } else {
            for (FieldGoalQuantity fieldGoalQuantity : fieldGoalQuantities) {
                if (fieldGoalQuantity.getField().equals(doctor.getFieldName())) {
                    ContractFieldAmount contractFieldAmount = fieldGoalQuantity.getContractFieldAmount();
                    contractFieldAmount.setAmount(contractFieldAmount.getAmount() + 1);
                    contractFieldAmountRepository.save(contractFieldAmount);
                    fieldGoalQuantity.setContractFieldAmount(contractFieldAmount);
                    fieldGoalQuantityRepository.save(fieldGoalQuantity);
                }
            }
            managerGoal.setFieldGoalQuantities(fieldGoalQuantities);
            managerGoalRepository.save(managerGoal);
        }
        System.out.println("Contract: 5555555555555555555555555555555555555555555555555555555555");
        //Contract
        Contract contract = new Contract();
        contract.setDoctor(doctor);
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());
        contract.setCreatedAt(LocalDate.now());
        contract.setStatus(GoalStatus.APPROVED);
        contract.setManager(managerGoal != null ? managerGoal.getManagerId() : null);
        contract.setContractType(contractDTO.getContractType());
        contractRepository.save(contract);

        System.out.println("New Contract: 66666666666666666666666666666666666666666666666666666666666");
        List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contractDTO.getMedicineWithQuantityDoctorDTOS().stream()
                .map(dto -> {
                    System.out.println("Pre Medicine 777777777777777777");
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new DoctorContractException("Medicine not found"));

                    System.out.println("After Medicine 88888888888888888888888888888888");
                    boolean isQuantityDoctorMedicineExists = medicineWithQuantityDoctorRepository.findByMedicineIdAndContractId(medicine.getId(), contract.getId()).isPresent();
                    if (isQuantityDoctorMedicineExists) {return null;}
                    MedicineWithQuantityDoctor medicineWithQuantityDoctor = new MedicineWithQuantityDoctor();
                    medicineWithQuantityDoctor.setMedicine(medicine);
                    medicineWithQuantityDoctor.setQuote(dto.getQuote());
                    medicineWithQuantityDoctor.setCorrection(dto.getQuote());
                    medicineWithQuantityDoctor.setDoctorContract(contract);

                    ContractMedicineDoctorAmount contractMedicineDoctorAmount = new ContractMedicineDoctorAmount();
                    contractMedicineDoctorAmount.setAmount(0L);
                    contractMedicineDoctorAmountRepository.save(contractMedicineDoctorAmount);
                    medicineWithQuantityDoctor.setContractMedicineDoctorAmount(contractMedicineDoctorAmount);
                    System.out.println("After  Contract  9999999999999999999999999999999999");

                    MedicineManagerGoalQuantity medicineGoalQuantity = null;
                    if (managerGoal != null) {
                        medicineGoalQuantity = medicineManagerGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(medicine.getId(), managerGoal.getGoalId())
                                .orElse(null);
                    }
                    if (medicineGoalQuantity != null) {
                        medicineWithQuantityDoctor.setContractMedicineManagerAmount(medicineGoalQuantity.getContractMedicineManagerAmount());
                    }
                    medicineWithQuantityDoctorRepository.save(medicineWithQuantityDoctor);
                    System.out.println("Sve Medicine 1010101010101010110101010101010");
                    return medicineWithQuantityDoctor;
                })
                .collect(Collectors.toList());
        contract.setMedicineWithQuantityDoctors(medicineWithQuantityDoctors);
        Contract savedContract = contractRepository.save(contract);
        System.out.println("save shiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiit");
        return convertToDTO(savedContract);
    }


    public ContractDTO medAgentCreateContractIfGoalExists(ContractDTO contractDTO) {
        if (contractRepository.findActiveOrPendingContractByDoctorId(contractDTO.getDoctorId()).isPresent()) {
            throw new DoctorContractExistsException("Doctor had already assigned contract doctorId:" + contractDTO.getDoctorId());
        }
        System.out.println("11111111111111111111111111111111111111111111111111111111");
        // Fetch the agent contract based on the agentId
        AgentGoal agentGoal = agentGoalRepository.findById(contractDTO.getAgentContractId())
                .orElse(null);
        ManagerGoal managerGoal = agentGoal != null ? agentGoal.getManagerGoal() : null;
        if (managerGoal == null) {
//            throw new DoctorContractException("AgentGoal does not have an associated ManagerGoal.");
        }
        System.out.println("22222222222222222222222222222222222222222222222222222222222222222222222222222222222");
        // Fetch the doctor based on doctorId
        User doctor = userRepository.findById(contractDTO.getDoctorId())
                .orElseThrow(() -> new DoctorContractException("Doctor not found"));
        if (agentGoal != null && agentGoal.getDistrictGoalQuantity() != null && agentGoal.getDistrictGoalQuantity().getDistrict() != null && doctor.getDistrict().getId() == agentGoal.getDistrictGoalQuantity().getDistrict().getId()) {
            ContractDistrictAmount contractDistrictAmount = agentGoal.getDistrictGoalQuantity().getContractDistrictAmount();
            contractDistrictAmount.setAmount(contractDistrictAmount.getAmount() + 1);
            contractDistrictAmountRepository.save(contractDistrictAmount);
        }

        // Fetch fields from ManagerGoal
        System.out.println("333333333333333333333333333333333333333333333333333333333333");
        List<FieldGoalQuantity> fieldGoalQuantities = managerGoal != null ? managerGoal.getFieldGoalQuantities() : null;
        if (fieldGoalQuantities == null || fieldGoalQuantities.isEmpty()) {

//            throw new DoctorContractException("No fields found in ManagerGoal.");
        } else {

            for (FieldGoalQuantity fieldGoalQuantity : fieldGoalQuantities) {
                if (fieldGoalQuantity.getField().equals(doctor.getFieldName())) {
                    // Field already exists, update amounts
                    ContractFieldAmount contractFieldAmount = fieldGoalQuantity.getContractFieldAmount();
                    contractFieldAmount.setAmount(contractFieldAmount.getAmount() + 1);
                    contractFieldAmountRepository.save(contractFieldAmount);
                }
            }

            List<FieldWithQuantity> fieldWithQuantities = agentGoal != null ? agentGoal.getFieldWithQuantities() : null;
            System.out.println("4444444444444444444444444444444444444444444444444444444444");

            if (fieldWithQuantities != null) {
                boolean isExists = fieldWithQuantities.stream()
                        .noneMatch(fieldWithQuantity -> fieldWithQuantity.getField().equals(doctor.getFieldName()));
                if (isExists) {

                    ContractFieldAmount newMedAgentFieldAmount = new ContractFieldAmount();
                    newMedAgentFieldAmount.setAmount(1l);
                    contractFieldAmountRepository.save(newMedAgentFieldAmount);
                    FieldWithQuantity newFieldWithQuantity = new FieldWithQuantity();
                    newFieldWithQuantity.setField(doctor.getFieldName());
                    newFieldWithQuantity.setQuote(0l);
                    newFieldWithQuantity.setAgentGoal(agentGoal);
                    newFieldWithQuantity.setContractFieldMedAgentAmount(newMedAgentFieldAmount);
                    fieldWithQuantityRepository.save(newFieldWithQuantity);
                    fieldWithQuantities.add(newFieldWithQuantity);

                }
                System.out.println("55555555555555555555555555555555555555555555555555555555");
                for (FieldWithQuantity fieldWithQuantity : fieldWithQuantities) {
                    if (fieldWithQuantity.getField().equals(doctor.getFieldName())) {
                        ContractFieldAmount medAgentAmount = fieldWithQuantity.getContractFieldMedAgentAmount();
                        medAgentAmount.setAmount(medAgentAmount.getAmount() + 1);
                    }
                }

            } else {
                System.out.println("66666666666666666666666666666666");
                fieldWithQuantities = new ArrayList<>();
                ContractFieldAmount newMedAgentFieldAmount = new ContractFieldAmount();
                newMedAgentFieldAmount.setAmount(1l);
                contractFieldAmountRepository.save(newMedAgentFieldAmount);
                FieldWithQuantity newFieldWithQuantity = new FieldWithQuantity();
                newFieldWithQuantity.setField(doctor.getFieldName());
                newFieldWithQuantity.setQuote(0l);
                newFieldWithQuantity.setAgentGoal(agentGoal);
                newFieldWithQuantity.setContractFieldMedAgentAmount(newMedAgentFieldAmount);
                fieldWithQuantityRepository.save(newFieldWithQuantity);
                fieldWithQuantities.add(newFieldWithQuantity);


            }
            agentGoal.setFieldWithQuantities(fieldWithQuantities);

            System.out.println("777777777777777777777777777777777777777777777777777777777");
            agentGoalRepository.save(agentGoal);
        }
        // Create a new Contract instance
        Contract contract = new Contract();
        contract.setDoctor(doctor);
        contract.setAgentGoal(agentGoal);
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());
        contract.setCreatedAt(LocalDate.now());
        contract.setMedAgent(userRepository.findById(contractDTO.getAgentId()).orElse(null));
        contract.setManager(managerGoal!=null?managerGoal.getManagerId():null);
        contract.setContractType(contractDTO.getContractType());
        contractRepository.save(contract);
        System.out.println("888888888888888888888888888888888888888888888888888888888888888888");
        List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contractDTO.getMedicineWithQuantityDoctorDTOS().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new DoctorContractException("Medicine not found"));
                    boolean isQuantityDoctorMedicineExists = medicineWithQuantityDoctorRepository.findByMedicineIdAndContractId(medicine.getId(), contract.getId()).isPresent();
                    if (isQuantityDoctorMedicineExists) {return null;}
                    MedicineWithQuantityDoctor medicineWithQuantityDoctor = new MedicineWithQuantityDoctor();
                    medicineWithQuantityDoctor.setMedicine(medicine);
                    medicineWithQuantityDoctor.setQuote(dto.getQuote());
                    medicineWithQuantityDoctor.setCorrection(dto.getQuote());
                    medicineWithQuantityDoctor.setDoctorContract(contract);
                    System.out.println("99999999999999999999999999999999999999999999999999999999999");
                    ContractMedicineDoctorAmount contractMedicineDoctorAmount = new ContractMedicineDoctorAmount();
                    contractMedicineDoctorAmount.setAmount(0L);
                    contractMedicineDoctorAmountRepository.save(contractMedicineDoctorAmount);
                    medicineWithQuantityDoctor.setContractMedicineDoctorAmount(contractMedicineDoctorAmount);

                    MedicineManagerGoalQuantity medicineManagerGoalQuantity = managerGoal!=null? medicineManagerGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(medicine.getId(), managerGoal.getGoalId())
                            .orElse(null):null;
                    if (medicineManagerGoalQuantity != null) {
                        medicineWithQuantityDoctor.setContractMedicineManagerAmount(medicineManagerGoalQuantity.getContractMedicineManagerAmount());
                    }
                    System.out.println("1010101010101010101001010101010101011010101101");
                    MedicineAgentGoalQuantity medicineAgentGoalQuantity =agentGoal!=null? medicineAgentGoalQuantityRepository
                            .findContractMedicineAmountByMedicineIdAndContractId(dto.getMedicineId(), agentGoal.getId()).orElse(null):null;
                    if (medicineAgentGoalQuantity != null) {
                        medicineWithQuantityDoctor.setContractMedicineMedAgentAmount(medicineAgentGoalQuantity.getContractMedicineMedAgentAmount());
                    }
                    medicineWithQuantityDoctorRepository.save(medicineWithQuantityDoctor);
                    return medicineWithQuantityDoctor;
                })
                .collect(Collectors.toList());
        System.out.println("1212121212121212121212112121");
        contract.setMedicineWithQuantityDoctors(medicineWithQuantityDoctors);


        Contract savedContract = contractRepository.save(contract);

        return convertToDTO(savedContract);
    }

    @Transactional
    public ContractDTO updateContract(Long contractId, ContractDTO contractDTO) {
        // Fetch the existing Contract entity
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new DoctorContractException("Contract not found"));

        // Update contract details
        if (contractDTO.getStartDate() != null) {
            contract.setStartDate(contractDTO.getStartDate());
        }
        if (contractDTO.getEndDate() != null) {
            contract.setEndDate(contractDTO.getEndDate());
        }
        if (contractDTO.getContractType() != null) {
            contract.setContractType(contractDTO.getContractType());
        }

        // Get existing medicines and their IDs
        List<MedicineWithQuantityDoctor> existingMedicines = contract.getMedicineWithQuantityDoctors();
        Set<Long> dtoMedicineIds = contractDTO.getMedicineWithQuantityDoctorDTOS() != null
                ? contractDTO.getMedicineWithQuantityDoctorDTOS().stream()
                .filter(dto -> dto != null && dto.getMedicineId() != null)
                .map(MedicineWithQuantityDoctorDTO::getMedicineId)
                .collect(Collectors.toSet())
                : Set.of();

        // Update or add medicines
        if (contractDTO.getMedicineWithQuantityDoctorDTOS() != null) {
            for (MedicineWithQuantityDoctorDTO dto : contractDTO.getMedicineWithQuantityDoctorDTOS()) {
                if (dto == null || dto.getMedicineId() == null) {
                    continue; // Skip null DTOs
                }

                Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                        .orElseThrow(() -> new DoctorContractException("Medicine not found with ID: " + dto.getMedicineId()));

                // Find existing MedicineWithQuantityDoctor by medicineId
                MedicineWithQuantityDoctor existingMedicine = existingMedicines.stream()
                        .filter(m -> m.getMedicine() != null && m.getMedicine().getId().equals(dto.getMedicineId()))
                        .findFirst()
                        .orElse(null);

                if (existingMedicine != null) {
                    // Update existing entry
                    if (dto.getQuote() != null && existingMedicine.getContractMedicineDoctorAmount() != null) {
                        if (dto.getQuote() >= existingMedicine.getContractMedicineDoctorAmount().getAmount()) {
                            existingMedicine.setQuote(dto.getQuote());
                            existingMedicine.setCorrection(dto.getQuote());
                        } else {
                            throw new DoctorContractException("Quote for medicine ID " + dto.getMedicineId() + " is less than required amount");
                        }
                    }
                } else {
                    // Add new entry
                    MedicineWithQuantityDoctor newMedicine = new MedicineWithQuantityDoctor();
                    newMedicine.setMedicine(medicine);
                    newMedicine.setQuote(dto.getQuote());
                    newMedicine.setCorrection(dto.getQuote());
                    ContractMedicineDoctorAmount contractMedicineDoctorAmount = new ContractMedicineDoctorAmount();
                    contractMedicineDoctorAmount.setAmount(0L);
                    contractMedicineDoctorAmountRepository.save(contractMedicineDoctorAmount);
                    newMedicine.setContractMedicineDoctorAmount(contractMedicineDoctorAmount);

                    newMedicine.setDoctorContract(contract);
                    medicineWithQuantityDoctorRepository.save(newMedicine);

                    existingMedicines.add(newMedicine);
                }
            }
        }
        // Save the contract (cascades to MedicineWithQuantityDoctor due to cascade=ALL)
        Contract save = contractRepository.save(contract);
        return convertToDTO(save);
    }


    @Transactional
    public void deleteContract(Long contractId) {
        try {
            Optional<Contract> contractOpt = contractRepository.findById(contractId);
            if (contractOpt.isEmpty()) {
                throw new DoctorContractException("Contract with ID " + contractId + " not found");
            }

            Contract contract = contractOpt.get();

            // 1. Delete MedicineWithQuantityDoctor records using native query to avoid cascading
            medicineWithQuantityDoctorRepository.deleteByContractIdNative(contractId);

            // 2. Clear AgentGoal reference in Contract
            if (contract.getAgentGoal() != null) {
                contract.setAgentGoal(null); // Remove reference to AgentGoal
                contractRepository.saveAndFlush(contract); // Save to update the foreign key
            }

            // 3. Delete the Contract
            contractRepository.deleteById(contractId);

        } catch (Exception e) {
            throw new DoctorContractException("Failed to delete Contract with ID " + contractId + ": " + e.getMessage());
        }
    }

    public ContractDTO convertToDTO(Contract contract) {
        if (contract == null) {
            throw new DoctorContractException("Contract cannot be null");
        }
        try {
            return new ContractDTO(
                    contract.getId(),
                    contract.getDoctor() != null ? contract.getDoctor().getUserId() : null,
                    contract.getStatus(),
                    contract.getCreatedAt(),
                    contract.getStartDate(),
                    contract.getEndDate(),
                    contract.getMedAgent() != null && contract.getMedAgent().getUserId() != null
                            ? contract.getMedAgent().getUserId()
                            : null,
                    contract.getAgentGoal() != null && contract.getAgentGoal().getId() != null
                            ? contract.getAgentGoal().getId()
                            : null,
                    contract.getManager() != null && contract.getManager().getUserId() != null
                            ? contract.getManager().getUserId()
                            : null,
                    contract.getContractType(),
                    contract.getMedicineWithQuantityDoctors() != null
                            ? contract.getMedicineWithQuantityDoctors().stream()
                            .filter(Objects::nonNull) // Avoid NullPointerException inside stream
                            .map(medicineWithQuantityDoctor -> new MedicineWithQuantityDoctorDTO(
                                    medicineWithQuantityDoctor.getId(),
                                    medicineWithQuantityDoctor.getMedicine() != null ? medicineWithQuantityDoctor.getMedicine().getId() : null,
                                    medicineWithQuantityDoctor.getQuote(),
                                    medicineWithQuantityDoctor.getCorrection(),
                                    medicineWithQuantityDoctor.getDoctorContract() != null && medicineWithQuantityDoctor.getDoctorContract().getAgentGoal() != null ? medicineWithQuantityDoctor.getDoctorContract().getAgentGoal().getId() : null,
                                    medicineWithQuantityDoctor.getContractMedicineDoctorAmount(),
                                    medicineWithQuantityDoctor.getMedicine()
                            ))
                            .collect(Collectors.toList())
                            : Collections.emptyList(),
                    districtRegionService.regionDistrictDTO(contract.getDoctor() !=null ?contract.getDoctor().getDistrict():null),
                    userService.convertToDTO(contract.getDoctor())
            );
        } catch (Exception e) {
            throw new DoctorContractException("Converting problem server error");
        }
    }


    public Page<ContractDTO> getContractsByStatus(GoalStatus goalStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contract> contracts = contractRepository.findByStatus(goalStatus, pageable);
        return contracts.map(this::convertToDTO);
    }

    public Page<ContractDTO> getAllContractsByAgent(UUID agentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return contractRepository.findAllContractsByAgent(agentId, pageable)
                .map(this::convertToDTO);
    }

    public void saveContractMedicineAmount(UUID doctorId, List<Long> medicineIds) {
        // Fetch active contract for the doctor
        Contract contract = contractRepository.findActiveContractByDoctorId(doctorId).orElse(null);
        List<OutOfContractMedicineAmount> outOfContractMedicines = outOfContractMedicineAmountRepository.findAllForDoctorThisMonth(doctorId).orElse(null);
        System.out.println("44444444444444444444444444444444444444444444444444---------------------------------------");

        if (contract == null) {
            System.out.println("5555555555555555555555555555555555555555555555555555---------------------------------------");

            for (Long medicineId : medicineIds) {
                Optional<OutOfContractMedicineAmount> existingMedicine = outOfContractMedicines.stream()
                        .filter(m -> m.getMedicine().getId().equals(medicineId))
                        .findFirst();

                if (existingMedicine.isPresent()) {
                    OutOfContractMedicineAmount outOfContractMedicine = existingMedicine.get();
                    outOfContractMedicine.setAmount(outOfContractMedicine.getAmount() + 1);
                    outOfContractMedicineAmountRepository.save(outOfContractMedicine);
                } else {
                    Medicine medicine = medicineRepository.findById(medicineId)
                            .orElseThrow(() -> new DoctorContractException("Medicine not found with ID: " + medicineId));

                    OutOfContractMedicineAmount newOutOfContractMedicine = new OutOfContractMedicineAmount();
                    newOutOfContractMedicine.setAmount(1L);
                    newOutOfContractMedicine.setDoctor(userRepository.findById(doctorId).orElseThrow(() -> new ContractNotFoundException("Doctor not found with ID: " + doctorId)));
                    newOutOfContractMedicine.setCreatedAt(LocalDate.now());
                    newOutOfContractMedicine.setMedicine(medicine);

                    outOfContractMedicineAmountRepository.save(newOutOfContractMedicine);
                }
            }
        } else {
            System.out.println("666666666666666666666666666666666666666666666666666666666666---------------------------------------");

            List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contract.getMedicineWithQuantityDoctors();

            if (medicineWithQuantityDoctors == null || medicineWithQuantityDoctors.isEmpty()) {
                throw new DoctorContractException("No medicines found for the contract of doctor ID: " + doctorId);
            }
            System.out.println("00000000000000000000000000000000000000000000000000000000000000000000000000000000000---------------------------------------");

            // Convert existing medicines to a map for quick lookup
            Map<Long, MedicineWithQuantityDoctor> medicineMap = medicineWithQuantityDoctors.stream()
                    .collect(Collectors.toMap(m -> m.getMedicine().getId(), m -> m));

            // Fetch existing out-of-contract medicines
            System.out.println("77777777777777777777777777777777777777777777777777777777777777---------------------------------------");

            for (Long medicineId : medicineIds) {
                MedicineWithQuantityDoctor medicineEntry = medicineMap.get(medicineId);

                if (medicineEntry != null && medicineEntry.getQuote() > medicineEntry.getContractMedicineDoctorAmount().getAmount()) {
                    // Medicine exists in contract, update amounts
                    ContractMedicineDoctorAmount doctorAmount = medicineEntry.getContractMedicineDoctorAmount();

                    System.out.println("888888888888888888888888888888888888888888888888888888888888888888888888---------------------------------------");

                    doctorAmount.setAmount(doctorAmount.getAmount() + 1);
                    if (medicineEntry.getContractMedicineManagerAmount() != null) {
                        ContractMedicineManagerAmount managerAmount = medicineEntry.getContractMedicineManagerAmount();
                        managerAmount.setAmount(managerAmount.getAmount() + 1);
                        contractMedicineManagerAmountRepository.save(managerAmount);
                    }
                    if (medicineEntry.getContractMedicineMedAgentAmount() != null) {
                        ContractMedicineMedAgentAmount medAgentAmount = medicineEntry.getContractMedicineMedAgentAmount();
                        medAgentAmount.setAmount(medAgentAmount.getAmount() + 1);
                        contractMedicineMedAgentAmountRepository.save(medAgentAmount);
                    }

                    System.out.println("9999999999999999999999999999999999999999999999999999999999999---------------------------------------");

                    contractMedicineDoctorAmountRepository.save(doctorAmount);
                } else {
                    // Check if the medicine is already in out-of-contract list
                    Optional<OutOfContractMedicineAmount> existingMedicine = outOfContractMedicines.stream()
                            .filter(m -> m.getMedicine().getId().equals(medicineId))
                            .findFirst();
                    System.out.println("1-11-1-1-1-1-1--1-1-1-1-1-1-1-000000000000000000000000000000000---------------------------------------");

                    if (existingMedicine.isPresent()) {
                        // If medicine exists, increment the amount
                        OutOfContractMedicineAmount outOfContractMedicine = existingMedicine.get();
                        outOfContractMedicine.setAmount(outOfContractMedicine.getAmount() + 1);
                        outOfContractMedicineAmountRepository.save(outOfContractMedicine);
                    } else {
                        // If medicine does not exist, create a new entry
                        Medicine medicine = medicineRepository.findById(medicineId)
                                .orElseThrow(() -> new DoctorContractException("Medicine not found with ID: " + medicineId));

                        OutOfContractMedicineAmount newOutOfContractMedicine = new OutOfContractMedicineAmount();
                        newOutOfContractMedicine.setAmount(1L);
                        newOutOfContractMedicine.setDoctor(contract.getDoctor());
                        newOutOfContractMedicine.setCreatedAt(LocalDate.now());
                        newOutOfContractMedicine.setMedicine(medicine);

                        outOfContractMedicineAmountRepository.save(newOutOfContractMedicine);
                    }

                    System.out.println("12           12                   12            12---------------------------------------");

                }
            }
        }
    }


    public OutOfContractAmountDTO getOutOfContractsByDoctorId(UUID doctorId) {
        List<OutOfContractMedicineAmountDTO> outOfContractMedicineAmountDTOs = outOfContractMedicineAmountRepository.findAllForDoctorThisMonth(doctorId).orElse(null).stream()
                .map(amount -> new OutOfContractMedicineAmountDTO(amount.getId(), amount.getAmount(), amount.getMedicine())) // mapping to DTO
                .collect(Collectors.toList());

        return new OutOfContractAmountDTO(
                doctorId,
                outOfContractMedicineAmountDTOs
        );
    }

    public ContractAmountDTO getContractById(Long contractId) {
        Optional<Contract> contractOptional = contractRepository.findById(contractId);
        if (contractOptional.isEmpty()) {
            throw new ContractNotFoundException("Contract not found");
        }

        Contract contract = contractOptional.get();

        // Mapping Contract to ContractAmountDTO
        ContractAmountDTO contractDTO = new ContractAmountDTO();
        contractDTO.setId(contract.getId());
        contractDTO.setDoctorId(contract.getDoctor().getUserId());
        contractDTO.setCreatedAt(contract.getCreatedAt());
        contractDTO.setStartDate(contract.getStartDate());
        contractDTO.setContractType(contract.getContractType());
        contractDTO.setEndDate(contract.getEndDate());
        contractDTO.setAgentId(contract.getAgentGoal() != null ? contract.getAgentGoal().getMedAgent().getUserId() : null);

        // Mapping contracted medicines (MedicineWithQuantityDTO)
        List<MedicineWithQuantityDoctorDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDoctorDTO(med.getId(), med.getMedicine().getId(), med.getQuote(), med.getCorrection(), med.getDoctorContract().getAgentGoal().getId(), med.getContractMedicineDoctorAmount(), med.getMedicine())) // mapping to DTO
                .collect(Collectors.toList());

        contractDTO.setMedicineWithQuantityDoctorDTOS(contractedMedicineWithQuantity);
        return contractDTO;
    }

    public ContractAmountDTO getContractByDoctorId(UUID doctorId) {
        Optional<Contract> contractOptional = contractRepository.findActiveOrPendingContractByDoctorId(doctorId);
        if (contractOptional.isEmpty()) {
            throw new ContractNotFoundException("Contracts not found for doctorId: " + doctorId);
        }

        Contract contract = contractOptional.get();

        ContractAmountDTO contractDTO = new ContractAmountDTO();
        contractDTO.setId(contract.getId());
        contractDTO.setDoctorId(contract.getDoctor().getUserId());
        contractDTO.setCreatedAt(contract.getCreatedAt());
        contractDTO.setStartDate(contract.getStartDate());
        contractDTO.setContractType(contract.getContractType());
        contractDTO.setEndDate(contract.getEndDate());
        contractDTO.setAgentId(contract.getAgentGoal() != null ? contract.getAgentGoal().getMedAgent().getUserId() : null);

        List<MedicineWithQuantityDoctorDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDoctorDTO(med.getId(), med.getMedicine().getId(),
                        med.getQuote(), med.getCorrection(), med.getDoctorContract().getAgentGoal() != null ? med.getDoctorContract().getAgentGoal().getId() : null, med.getContractMedicineDoctorAmount(), med.getMedicine())) // mapping to DTO
                .collect(Collectors.toList());

        contractDTO.setMedicineWithQuantityDoctorDTOS(contractedMedicineWithQuantity);


        return contractDTO;
    }

    public ContractAmountDTO getActiveContractByDoctorId(UUID doctorId) {
        Optional<Contract> contractOptional = contractRepository.findActiveContractByDoctorId(doctorId);
        if (contractOptional.isEmpty()) {
            throw new ContractNotFoundException("Contracts not found for doctorId: " + doctorId);
        }

        Contract contract = contractOptional.get();

        ContractAmountDTO contractDTO = new ContractAmountDTO();
        contractDTO.setId(contract.getId());
        contractDTO.setDoctorId(contract.getDoctor().getUserId());
        contractDTO.setCreatedAt(contract.getCreatedAt());
        contractDTO.setStartDate(contract.getStartDate());
        contractDTO.setEndDate(contract.getEndDate());
        contractDTO.setContractType(contract.getContractType());
        contractDTO.setAgentId(contract.getAgentGoal() != null ? contract.getAgentGoal().getMedAgent().getUserId() : null);
        System.out.println("--------------------------------11111111111111111111111111111111");
        List<MedicineWithQuantityDoctorDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDoctorDTO(med.getId(), med.getMedicine().getId(),
                        med.getQuote(), med.getCorrection(), med.getDoctorContract().getAgentGoal() != null ? med.getDoctorContract().getAgentGoal().getId() : null, med.getContractMedicineDoctorAmount(), med.getMedicine())) // mapping to DTO
                .collect(Collectors.toList());

        contractDTO.setMedicineWithQuantityDoctorDTOS(contractedMedicineWithQuantity);
        System.out.println("----------------------------22222222222222222222222222222222222222222222222222");

        return contractDTO;
    }

    public void enableContract(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));

        if (contract.getStatus() == GoalStatus.APPROVED) {
            throw new IllegalStateException("Contract is already approved.");
        }

        contract.setStatus(GoalStatus.APPROVED);
        contractRepository.save(contract);
    }

    public void declineContract(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));

        if (contract.getStatus() == GoalStatus.DECLINED) {
            throw new DoctorContractException("Contract is already declined.");
        }

        contract.setStatus(GoalStatus.DECLINED);
        contractRepository.save(contract);
    }

    public DoctorRecipeStatsDTO getDoctorRecipeStatsDTOByDoctorId(UUID doctorId) {
        DoctorRecipeStatsDTO doctorRecipeStatsDTO = new DoctorRecipeStatsDTO();
        doctorRecipeStatsDTO.setDoctorId(doctorId);
        doctorRecipeStatsDTO.setRecipesCreatedThisMonth(recipeRepository.countRecipesCreatedThisMonthByDoctor(doctorId));
        doctorRecipeStatsDTO.setAverageRecipesPerMonth(recipeRepository.averageRecipesLast12MonthsByDoctor(doctorId));
        return doctorRecipeStatsDTO;
    }

    public List<ContractDTO> getContractsByMedAgent(UUID medAgentId, Long regionId, Long districtId, Long workPlaceId,
                                                    String firstName, String lastName, String middleName, Field fieldName) {
        List<Contract> contracts = contractRepository.findAllByMedAgentId(medAgentId, regionId, districtId, workPlaceId,
                firstName, lastName, middleName, fieldName);
        return contracts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<ContractDTO> getFilteredContracts(Long regionId, Long districtId, Long workPlaceId,
                                                  String firstName, String lastName, String middleName,
                                                  Field fieldName, LocalDate startDate,
                                                  LocalDate endDate, Long medicineId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Contract> contractPage = contractRepository.findContracts(regionId, districtId, workPlaceId,
                firstName, lastName, middleName,
                fieldName, startDate, endDate,
                pageable);

        // Convert each Contract entity to DTO and maintain pagination
        return contractPage.map(this::convertToDTO);
    }

    public List<LineChart> getDoctorRecipeChart(UUID doctorId, LocalDate startDate, LocalDate endDate, int numberOfParts) {
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

}
