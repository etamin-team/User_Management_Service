package com.example.user_management_service.service;

import com.example.user_management_service.exception.ContractNotFoundException;
import com.example.user_management_service.exception.DoctorContractExistsException;
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
    private MedicineWithQuantityRepository medicineWithQuantityRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private AgentContractRepository agentContractRepository;
    private final ContractMedicineAmountRepository contractMedicineAmountRepository;
    private final MedicineGoalQuantityRepository medicineGoalQuantityRepository;
    private final ContractFieldAmountRepository contractFieldAmountRepository;


    // Doctor Contract

    public ContractDTO createContract(ContractDTO contractDTO) {
        if (contractRepository.findActiveContractByDoctorId(contractDTO.getDoctorId()).isPresent()){
            throw new DoctorContractExistsException("Doctor had already assigned contract doctorId:"+contractDTO.getDoctorId());
        }
        // Fetch the agent contract based on the agentId
        AgentContract agentContract = agentContractRepository.findById(contractDTO.getAgentContractId())
                .orElseThrow(() -> new RuntimeException("AgentContract not found"));
        ManagerGoal managerGoal = agentContract.getManagerGoal();
        if (managerGoal == null) {
            throw new IllegalStateException("AgentContract does not have an associated ManagerGoal.");
        }

        // Fetch fields from ManagerGoal
        List<FieldGoalQuantity> fieldGoalQuantities = managerGoal.getFieldGoalQuantities();
        if (fieldGoalQuantities == null || fieldGoalQuantities.isEmpty()) {
            throw new IllegalStateException("No fields found in ManagerGoal.");
        }

        List<FieldWithQuantity> fieldWithQuantities = agentContract.getFieldWithQuantities();
        if (fieldWithQuantities == null) {
            fieldWithQuantities = new ArrayList<>();
        }

        // Convert existing fields to a map for quick lookup
        Map<Field, FieldWithQuantity> fieldMap = fieldWithQuantities.stream()
                .collect(Collectors.toMap(FieldWithQuantity::getField, f -> f));

        for (FieldGoalQuantity fieldGoalQuantity : fieldGoalQuantities) {
            Field field = fieldGoalQuantity.getField();
            FieldWithQuantity fieldEntry = fieldMap.get(field);

            if (fieldEntry != null) {
                // Field already exists, update amounts
                ContractFieldAmount contractFieldAmount = fieldEntry.getContractFieldAmount();
                ContractFieldAmount medAgentAmount = fieldEntry.getContractFieldMedAgentAmount();

                contractFieldAmount.setAmount(contractFieldAmount.getAmount() + 1);
                medAgentAmount.setAmount(medAgentAmount.getAmount() + 1);
            } else {
                // Create new FieldWithQuantity entry
                ContractFieldAmount newContractFieldAmount = new ContractFieldAmount();
                newContractFieldAmount.setAmount(1l);
                ContractFieldAmount newMedAgentFieldAmount = new ContractFieldAmount();
                newMedAgentFieldAmount.setAmount(1l);


                contractFieldAmountRepository.save(newContractFieldAmount);
                contractFieldAmountRepository.save(newMedAgentFieldAmount);

                FieldWithQuantity newFieldWithQuantity = new FieldWithQuantity();
                newFieldWithQuantity.setField(field);
                newFieldWithQuantity.setQuote(1l);
                newFieldWithQuantity.setAgentContract(agentContract);
                newFieldWithQuantity.setContractFieldAmount(newContractFieldAmount);
                newFieldWithQuantity.setContractFieldMedAgentAmount(newMedAgentFieldAmount);

                fieldWithQuantities.add(newFieldWithQuantity);
            }
        }

        agentContract.setFieldWithQuantities(fieldWithQuantities);
        agentContractRepository.save(agentContract);
        // Fetch the doctor based on doctorId
        User doctor = userRepository.findById(contractDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Create a new Contract instance

        Contract contract = new Contract();
        contract.setDoctor(doctor);
        contract.setAgentContract(agentContract);
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());
        contract.setCreatedAt(LocalDate.now());  // Current date as createdAt

        contractRepository.save(contract);
        List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contractDTO.getMedicinesWithQuantities().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("Medicine not found"));

                    MedicineWithQuantityDoctor medicineWithQuantityDoctor = new MedicineWithQuantityDoctor();
                    medicineWithQuantityDoctor.setMedicine(medicine);
                    medicineWithQuantityDoctor.setQuote(dto.getQuote());
                    medicineWithQuantityDoctor.setDoctorContract(contract);

                    ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                    contractMedicineAmount.setAmount(0L);
                    contractMedicineAmountRepository.save(contractMedicineAmount);
                    medicineWithQuantityDoctor.setContractMedicineDoctorAmount(contractMedicineAmount);

                    ContractMedicineAmount medicineGoalQuantity = medicineGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(medicine.getId(), agentContract.getManagerGoal().getGoalId())
                            .orElseThrow(() -> new RuntimeException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()));
                    medicineWithQuantityDoctor.setContractMedicineAmount(medicineGoalQuantity);
                    contractMedicineAmountRepository.save(medicineGoalQuantity);


                    ContractMedicineAmount contractMedicineMedAgentAmount = medicineWithQuantityRepository
                            .findContractMedicineAmountByMedicineIdAndContractId(dto.getMedicineId(), agentContract.getId()).orElseThrow(() -> new RuntimeException("No ContractMedicineAmount found for the given Medicine and AgentContract"));
                    medicineWithQuantityDoctor.setContractMedicineMedAgentAmount(contractMedicineMedAgentAmount);
                    contractMedicineAmountRepository.save(contractMedicineMedAgentAmount);

                    medicineWithQuantityDoctorRepository.save(medicineWithQuantityDoctor);
                    return medicineWithQuantityDoctor;
                })
                .collect(Collectors.toList());

        contract.setMedicineWithQuantityDoctors(medicineWithQuantityDoctors);

        Contract savedContract = contractRepository.save(contract);

        return convertToDTO(savedContract);
    }

    // Update an existing Contract
    public ContractDTO updateContract(Long contractId, ContractDTO contractDTO) {
        // Fetch the existing Contract entity
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        // Update contract details
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());
        contract.setAgentContract(agentContractRepository.findById(contractDTO.getAgentContractId())
                .orElseThrow(() -> new RuntimeException("AgentContract not found")));
        contract.setDoctor(userRepository.findById(contractDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found")));

        // Update or add medicines with quantities
        contract.setMedicineWithQuantityDoctors(contractDTO.getMedicinesWithQuantities().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("Medicine not found"));

                    MedicineWithQuantityDoctor existingMedicineWithQuantityDoctor = contract.getMedicineWithQuantityDoctors().stream()
                            .filter(m -> m.getMedicine().getId().equals(dto.getMedicineId()))
                            .findFirst()
                            .orElse(null);

                    if (existingMedicineWithQuantityDoctor != null) {
                        // Update existing medicine quantity
                        existingMedicineWithQuantityDoctor.setQuote(dto.getQuote());
                        return existingMedicineWithQuantityDoctor;
                    } else {
                        // Add new medicine with quantity
                        MedicineWithQuantityDoctor newMedicineWithQuantityDoctor = new MedicineWithQuantityDoctor();
                        newMedicineWithQuantityDoctor.setMedicine(medicine);
                        newMedicineWithQuantityDoctor.setQuote(dto.getQuote());
                        newMedicineWithQuantityDoctor.setDoctorContract(contract);
                        return newMedicineWithQuantityDoctor;
                    }
                })
                .collect(Collectors.toList()));

        // Save the updated contract
        Contract savedContract = contractRepository.save(contract);

        // Convert to DTO and return
        return convertToDTO(savedContract);
    }

    // Delete a Contract
    public void deleteContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        contractRepository.delete(contract);
    }

    private ContractDTO convertToDTO(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }

        return new ContractDTO(
                contract.getId(),
                contract.getDoctor() != null ? contract.getDoctor().getUserId() : null,
                contract.getCreatedAt(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getDoctor() != null && contract.getDoctor().getFieldName() != null
                        ? null //here is the problem
                        : null,
                contract.getAgentContract() != null && contract.getAgentContract().getMedAgent() != null
                        ? contract.getAgentContract().getMedAgent().getUserId()
                        : null,
                contract.getAgentContract() != null ? contract.getAgentContract().getId() : null,
                contract.getAgentContract() != null && contract.getAgentContract().getMedicinesWithQuantities() != null
                        ? contract.getAgentContract().getMedicinesWithQuantities().stream()
                        .filter(Objects::nonNull) // Avoid NullPointerException inside stream
                        .map(medicineWithQuantity -> new MedicineWithQuantityDTO(
                                medicineWithQuantity.getMedicine() != null ? medicineWithQuantity.getMedicine().getId() : null,
                                medicineWithQuantity.getMedicine() != null ? medicineWithQuantity.getMedicine().getName() : null,
                                medicineWithQuantity.getQuote(),
                                medicineWithQuantity.getContractMedicineAmount()
                        ))
                        .collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }

    public Page<ContractDTO> getPendingReviewContracts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contract> contracts = contractRepository.findByStatus(GoalStatus.PENDING_REVIEW, pageable);
        return contracts.map(this::convertToDTO);
    }

    public void saveContractMedicineAmount(UUID doctorId, List<Long> medicineIds) {
        // Fetch active contract for the doctor
        Contract contract = contractRepository.findActiveContractByDoctorId(doctorId)
                .orElseThrow(() -> new ContractNotFoundException("Contracts not found for doctor with ID: " + doctorId));

        List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contract.getMedicineWithQuantityDoctors();

        if (medicineWithQuantityDoctors == null || medicineWithQuantityDoctors.isEmpty()) {
            throw new IllegalStateException("No medicines found for the contract of doctor ID: " + doctorId);
        }

        // Convert existing medicines to a map for quick lookup
        Map<Long, MedicineWithQuantityDoctor> medicineMap = medicineWithQuantityDoctors.stream()
                .collect(Collectors.toMap(m -> m.getMedicine().getId(), m -> m));

        // Fetch existing out-of-contract medicines
        List<OutOfContractMedicineAmount> outOfContractMedicines = outOfContractMedicineAmountRepository.findAllForDoctorThisMonth(doctorId);

        for (Long medicineId : medicineIds) {
            MedicineWithQuantityDoctor medicineEntry = medicineMap.get(medicineId);

            if (medicineEntry != null&&medicineEntry.getQuote()>medicineEntry.getContractMedicineDoctorAmount().getAmount()) {
                // Medicine exists in contract, update amounts
                ContractMedicineAmount doctorAmount = medicineEntry.getContractMedicineDoctorAmount();
                ContractMedicineAmount medAgentAmount = medicineEntry.getContractMedicineMedAgentAmount();
                ContractMedicineAmount managerAmount = medicineEntry.getContractMedicineAmount();

                doctorAmount.setAmount(doctorAmount.getAmount() + 1);
                medAgentAmount.setAmount(medAgentAmount.getAmount() + 1);
                managerAmount.setAmount(managerAmount.getAmount() + 1);
            } else {
                // Check if the medicine is already in out-of-contract list
                Optional<OutOfContractMedicineAmount> existingMedicine = outOfContractMedicines.stream()
                        .filter(m -> m.getMedicine().getId().equals(medicineId))
                        .findFirst();

                if (existingMedicine.isPresent()) {
                    // If medicine exists, increment the amount
                    OutOfContractMedicineAmount outOfContractMedicine = existingMedicine.get();
                    outOfContractMedicine.setAmount(outOfContractMedicine.getAmount() + 1);
                } else {
                    // If medicine does not exist, create a new entry
                    Medicine medicine = medicineRepository.findById(medicineId)
                            .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + medicineId));

                    OutOfContractMedicineAmount newOutOfContractMedicine = new OutOfContractMedicineAmount();
                    newOutOfContractMedicine.setAmount(1L);
                    newOutOfContractMedicine.setDoctor(contract.getDoctor());
                    newOutOfContractMedicine.setMedicine(medicine);

                    outOfContractMedicineAmountRepository.save(newOutOfContractMedicine);
                }
            }
        }

        contractRepository.save(contract);
    }


    public OutOfContractAmountDTO getOutOfContractsByDoctorId(UUID doctorId){
        List<OutOfContractMedicineAmountDTO> outOfContractMedicineAmountDTOs = outOfContractMedicineAmountRepository.findAllForDoctorThisMonth(doctorId).stream()
                .map(amount -> new OutOfContractMedicineAmountDTO(amount.getId(), amount.getAmount(), amount.getMedicine().getId())) // mapping to DTO
                .collect(Collectors.toList());

        return new OutOfContractAmountDTO(
                doctorId,
                outOfContractMedicineAmountDTOs
        );
    }
    public ContractAmountDTO getContractById(Long contractId) {
        Optional<Contract> contractOptional = contractRepository.findById(contractId);
        if (contractOptional.isEmpty()) {
            throw new IllegalStateException("Contract not found");
        }

        Contract contract = contractOptional.get();

        // Mapping Contract to ContractAmountDTO
        ContractAmountDTO contractDTO = new ContractAmountDTO();
        contractDTO.setId(contract.getId());
        contractDTO.setDoctorId(contract.getDoctor().getUserId());
        contractDTO.setCreatedAt(contract.getCreatedAt());
        contractDTO.setStartDate(contract.getStartDate());
        contractDTO.setEndDate(contract.getEndDate());
        contractDTO.setAgentId(contract.getAgentContract() != null ? contract.getAgentContract().getId() : null);

        // Mapping contracted medicines (MedicineWithQuantityDTO)
        List<MedicineWithQuantityDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getMedicine().getId(),med.getMedicine().getName(), med.getContractMedicineAmount().getAmount(), med.getContractMedicineAmount())) // mapping to DTO
                .collect(Collectors.toList());

        contractDTO.setContractedMedicineWithQuantity(contractedMedicineWithQuantity);
        return contractDTO;
    }

    public ContractAmountDTO getContractByDoctorId(UUID doctorId) {
        Optional<Contract> contractOptional = contractRepository.findActiveContractByDoctorId(doctorId);
        if (contractOptional.isEmpty()) {
            throw new IllegalStateException("Contract not found for doctorId: " + doctorId);
        }

        Contract contract = contractOptional.get();

        ContractAmountDTO contractDTO = new ContractAmountDTO();
        contractDTO.setId(contract.getId());
        contractDTO.setDoctorId(contract.getDoctor().getUserId());
        contractDTO.setCreatedAt(contract.getCreatedAt());
        contractDTO.setStartDate(contract.getStartDate());
        contractDTO.setEndDate(contract.getEndDate());
        contractDTO.setAgentId(contract.getAgentContract() != null ? contract.getAgentContract().getId() : null);

        List<MedicineWithQuantityDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getId(), med.getMedicine().getName(),
                        med.getContractMedicineAmount().getAmount(), med.getContractMedicineAmount())) // mapping to DTO
                .collect(Collectors.toList());

        contractDTO.setContractedMedicineWithQuantity(contractedMedicineWithQuantity);


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
            throw new IllegalStateException("Contract is already declined.");
        }

        contract.setStatus(GoalStatus.DECLINED);
        contractRepository.save(contract);
    }
}
