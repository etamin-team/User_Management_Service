package com.example.user_management_service.service;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.ContractDTO;
import com.example.user_management_service.model.dto.MedicineWithQuantityDTO;
import com.example.user_management_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ContractService {


    private final ContractRepository contractRepository;
    private final FieldWithQuantityRepository fieldWithQuantityRepository;
    private MedicineWithQuantityRepository medicineWithQuantityRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private AgentContractRepository agentContractRepository;
    private final ContractMedicineAmountRepository contractMedicineAmountRepository;
    private final MedicineGoalQuantityRepository medicineGoalQuantityRepository;


    // Doctor Contract

    public ContractDTO createContract(ContractDTO contractDTO) {
        // Fetch the agent contract based on the agentId
        AgentContract agentContract = agentContractRepository.findById(contractDTO.getAgentId())
                .orElseThrow(() -> new RuntimeException("AgentContract not found"));

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

                    ContractMedicineAmount medicineGoalQuantity = medicineGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(medicine.getId(),agentContract.getManagerGoal().getGoalId())
                            .orElseThrow(() -> new RuntimeException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()));
                    medicineWithQuantityDoctor.setContractMedicineAmount(medicineGoalQuantity);


                    ContractMedicineAmount contractMedicineMedAgentAmount = medicineWithQuantityRepository
                            .findContractMedicineAmountByMedicineIdAndContractId(dto.getMedicineId(), agentContract.getId()).orElseThrow(() -> new RuntimeException("No ContractMedicineAmount found for the given Medicine and AgentContract"));
                    medicineWithQuantityDoctor.setContractMedicineAmount(contractMedicineMedAgentAmount);

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
        contract.setAgentContract(agentContractRepository.findById(contractDTO.getAgentId())
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
        return new ContractDTO(
                contract.getId(),
                contract.getDoctor().getUserId(),
                contract.getCreatedAt(),
                contract.getStartDate(),
                contract.getEndDate(),
                fieldWithQuantityRepository.findByField(contract.getDoctor().getFieldName()),
                contract.getAgentContract().getId(),
                contract.getAgentContract().getMedicinesWithQuantities().stream()
                        .map(fieldWithQuantity -> new MedicineWithQuantityDTO(
                                fieldWithQuantity.getMedicine().getId(),
                                fieldWithQuantity.getMedicine().getName(),
                                fieldWithQuantity.getQuote()
                        ))
                        .collect(Collectors.toList())
        );
    }

    public Page<ContractDTO> getPendingReviewContracts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contract> contracts = contractRepository.findByStatus(GoalStatus.PENDING_REVIEW, pageable);
        return contracts.map(this::convertToDTO);
    }
}
