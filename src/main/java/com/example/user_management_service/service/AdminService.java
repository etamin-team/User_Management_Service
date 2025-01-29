package com.example.user_management_service.service;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Date-12/20/2024
 * By Sardor Tokhirov
 * Time-3:37 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ManagerGoalRepository managerGoalRepository;  // Assuming this repository exists
    private final MedicineRepository medicineRepository;  // Assuming this repository exists
    private final DistrictRepository districtRepository;
    private final ContractMedicineAmountRepository contractMedicineAmountRepository;
    private final ContractFieldAmountRepository contractFieldAmountRepository;
    private final ContractDistrictAmountRepository contractDistrictAmountRepository;

    private AgentContractRepository agentContractRepository;
    private MedicineWithQuantityRepository medicineWithQuantityRepository;


    public Page<UserDTO> getDoctorsNotDeclinedAndNotEnabled(Pageable pageable) {
        return userRepository.findDoctorsByStatus(Role.DOCTOR, UserStatus.PENDING, pageable)
                .map(this::convertToDTO);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getDateOfBirth(),
                user.getPhoneNumber(),
                user.getNumber(),
                user.getEmail(),
                user.getPosition(),
                user.getFieldName(),
                user.getGender(),
                user.getStatus(),
                user.getCreatorId(),
                user.getWorkplace()==null ? null : user.getWorkplace().getId(),
                user.getDistrict().getId()
        );
    }

    public void enableUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.ENABLED);
        userRepository.save(user);
    }

    public void declineUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.DECLINED);
        userRepository.save(user);
    }
    @Transactional
    public ManagerGoalDTO createManagerGoal(ManagerGoalDTO managerGoalDTO) {
        ManagerGoal managerGoal = new ManagerGoal();
        managerGoal.setManagerId(userRepository.findById(managerGoalDTO.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found")));
        managerGoal.setCreatedAt(LocalDate.now());
        managerGoal.setStartDate(managerGoalDTO.getStartDate());
        managerGoal.setEndDate(managerGoalDTO.getEndDate());

        managerGoalRepository.save(managerGoal);

        managerGoal.setFieldGoalQuantities(managerGoalDTO.getFieldGoalQuantities().stream()
                .map(dto -> {
                    ContractFieldAmount contractFieldAmount = new ContractFieldAmount();
                    contractFieldAmount.setAmount(0L); // Or set it based on your logic
                    contractFieldAmountRepository.save(contractFieldAmount);
                    FieldGoalQuantity fieldGoalQuantity = new FieldGoalQuantity();
                    fieldGoalQuantity.setField(dto.getFieldName());
                    fieldGoalQuantity.setQuote(dto.getQuote());
                    fieldGoalQuantity.setManagerGoal(managerGoal);
                    fieldGoalQuantity.setContractFieldAmount(contractFieldAmount);
                    return fieldGoalQuantity;
                })
                .collect(Collectors.toList()));

        managerGoal.setManagerGoalQuantities(managerGoalDTO.getManagerGoalQuantities().stream()
                .map(dto -> {
                    ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                    contractMedicineAmount.setAmount(0L); // Or set it based on your logic
                    contractMedicineAmountRepository.save(contractMedicineAmount);
                    MedicineGoalQuantity medicineGoalQuantity = new MedicineGoalQuantity();
                    medicineGoalQuantity.setId(dto.getManagerGoalId());
                    medicineGoalQuantity.setQuote(dto.getQuote());
                    medicineGoalQuantity.setManagerGoal(managerGoal);
                    Medicine medicine=medicineRepository.getOne(dto.getMedicineId());
                    medicineGoalQuantity.setMedicine(medicine);
                    medicineGoalQuantity.setContractMedicineAmount(contractMedicineAmount);
                    return medicineGoalQuantity;
                })
                .collect(Collectors.toList()));

        managerGoal.setDistrictGoalQuantities(managerGoalDTO.getDistrictGoalQuantities().stream()
                .map(dto -> {
                    ContractDistrictAmount contractDistrictAmount = new ContractDistrictAmount();
                    contractDistrictAmount.setAmount(0L); // Or set it based on your logic
                    contractDistrictAmountRepository.save(contractDistrictAmount); // Save it to generate ID
                    DistrictGoalQuantity districtGoalQuantity = new DistrictGoalQuantity();
                    districtGoalQuantity.setDistrict(districtRepository.getById(dto.getDistrictId()));
                    districtGoalQuantity.setQuote(dto.getQuote());
                    districtGoalQuantity.setManagerGoal(managerGoal);
                    districtGoalQuantity.setContractDistrictAmount(contractDistrictAmount);
                    return districtGoalQuantity;
                })
                .collect(Collectors.toList()));

        ManagerGoal savedGoal = managerGoalRepository.save(managerGoal);
        return convertToDTO(savedGoal);
    }

    public Optional<ManagerGoalDTO> updateManagerGoal(Long id, ManagerGoalDTO updateGoalDTO) {
        return managerGoalRepository.findById(id)
                .map(existingGoal -> {
                    existingGoal.setStartDate(updateGoalDTO.getStartDate());
                    existingGoal.setEndDate(updateGoalDTO.getEndDate());

                    existingGoal.setFieldGoalQuantities(updateGoalDTO.getFieldGoalQuantities().stream()
                            .map(dto -> {
                                FieldGoalQuantity fieldGoalQuantity = new FieldGoalQuantity();
                                fieldGoalQuantity.setField(dto.getFieldName());
                                fieldGoalQuantity.setQuote(dto.getQuote());
                                fieldGoalQuantity.setManagerGoal(existingGoal);
                                return fieldGoalQuantity;
                            })
                            .collect(Collectors.toList()));

                    existingGoal.setManagerGoalQuantities(updateGoalDTO.getManagerGoalQuantities().stream()
                            .map(dto -> {
                                MedicineGoalQuantity medicineGoalQuantity = new MedicineGoalQuantity();
                                medicineGoalQuantity.setManagerGoal(existingGoal);
                                medicineGoalQuantity.setQuote(dto.getQuote());
                                medicineGoalQuantity.setManagerGoal(existingGoal);
                                return medicineGoalQuantity;
                            })
                            .collect(Collectors.toList()));

                    existingGoal.setDistrictGoalQuantities(updateGoalDTO.getDistrictGoalQuantities().stream()
                            .map(dto -> {
                                DistrictGoalQuantity districtGoalQuantity = new DistrictGoalQuantity();
                                districtGoalQuantity.setDistrict(districtRepository.getById(dto.getDistrictId()));
                                districtGoalQuantity.setQuote(dto.getQuote());
                                districtGoalQuantity.setManagerGoal(existingGoal);
                                return districtGoalQuantity;
                            })
                            .collect(Collectors.toList()));

                    ManagerGoal updatedGoal = managerGoalRepository.save(existingGoal);
                    return convertToDTO(updatedGoal);
                });
    }

    public boolean deleteManagerGoal(Long id) {
        if (managerGoalRepository.existsById(id)) {
            managerGoalRepository.deleteById(id);
            return true;
        }
        return false;
    }





    private ManagerGoalDTO convertToDTO(ManagerGoal goal) {
        return new ManagerGoalDTO(
                goal.getGoalId(),
                goal.getManagerId().getUserId(),
                goal.getFieldGoalQuantities().stream()
                        .map(q -> new FieldGoalQuantityDTO(q.getId(),q.getField(), q.getQuote(),q.getManagerGoal().getGoalId()))
                        .collect(Collectors.toList()),
                goal.getManagerGoalQuantities().stream()
                        .map(q -> new ManagerGoalQuantityDTO(q.getId(), q.getMedicine().getId(),q.getMedicine().getName(),q.getQuote(),q.getManagerGoal().getGoalId()

                        ))
                        .collect(Collectors.toList()),
                goal.getDistrictGoalQuantities().stream()
                        .map(q -> new DistrictGoalQuantityDTO(q.getId(),q.getDistrict().getId(),q.getDistrict().getName(),q.getQuote(), q.getManagerGoal().getGoalId()))
                        .collect(Collectors.toList()),
                goal.getCreatedAt(),
                goal.getStartDate(),
                goal.getEndDate()
        );
    }

    public AgentContractDTO convertToDTO(AgentContract agentContract) {
        List<MedicineWithQuantityDTO> medicineWithQuantityDTOs = agentContract.getMedicinesWithQuantities()
                .stream()
                .map(mwq -> new MedicineWithQuantityDTO(
                        mwq.getMedicine().getId(),
                        mwq.getMedicine().getName(),
                        mwq.getQuote()))
                .collect(Collectors.toList());

        return new AgentContractDTO(
                agentContract.getId(),
                agentContract.getTotalAmount(),
                agentContract.getCreatedAt(),
                agentContract.getStartDate(),
                agentContract.getEndDate(),
                agentContract.getMedAgent().getUserId(),
                medicineWithQuantityDTOs
        );
    }


    public List<AgentContractDTO> getAllAgentContracts() {
        List<AgentContract> contracts = agentContractRepository.findAll();
        return contracts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }




    public void deleteAgentContract(Long contractId) {
        // Check if contract exists
        AgentContract existingContract = agentContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Agent Contract not found"));

        // Delete the contract
        agentContractRepository.delete(existingContract);
    }




    public AgentContractDTO createAgentContract(AgentContractDTO agentContractDTO) {
        AgentContract agentContract = new AgentContract();
        agentContract.setTotalAmount(agentContractDTO.getTotalAmount());
        agentContract.setCreatedAt(LocalDate.now());
        agentContract.setStartDate(agentContractDTO.getStartDate());
        agentContract.setEndDate(agentContractDTO.getEndDate());

        // Set the MedAgent (user) associated with this contract using setter
        User medAgent = userRepository.findById(agentContractDTO.getMedAgentId())
                .orElseThrow(() -> new RuntimeException("MedAgent not found"));

        agentContract.setMedAgent(medAgent);

        // Handle medicines with quantities and associate them with the current AgentContract
        List<MedicineWithQuantity> medicineWithQuantities = agentContractDTO.getMedicinesWithQuantities().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("Medicine not found"));

                    // Create a new MedicineWithQuantity and set values
                    MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
                    medicineWithQuantity.setMedicine(medicine);
                    medicineWithQuantity.setQuote(dto.getQuote()); // Set the quantity
                    medicineWithQuantity.setAgentContract(agentContract); // Associate with AgentContract

                    return medicineWithQuantity;
                }).collect(Collectors.toList());

        // Set the medicines and their quantities in the AgentContract
        agentContract.setMedicinesWithQuantities(medicineWithQuantities);

        // Save the contract to the database
        agentContractRepository.save(agentContract);

        // Return DTO of created contract
        return convertToDTO(agentContract);
    }

    public AgentContractDTO updateAgentContract(Long contractId, AgentContractDTO agentContractDTO) {
        // Find the existing contract
        AgentContract existingContract = agentContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Agent Contract not found"));

        existingContract.setTotalAmount(agentContractDTO.getTotalAmount());
        existingContract.setStartDate(agentContractDTO.getStartDate());
        existingContract.setEndDate(agentContractDTO.getEndDate());

        // Update MedAgent (optional, depending on your logic)
        if (agentContractDTO.getMedAgentId() != null) {
            User medAgent = userRepository.findById(agentContractDTO.getMedAgentId())
                    .orElseThrow(() -> new RuntimeException("MedAgent not found"));
            existingContract.setMedAgent(medAgent);
        }

        // Update medicines with quantities and associate them with the existing AgentContract
        List<MedicineWithQuantity> updatedMedicinesWithQuantities = agentContractDTO.getMedicinesWithQuantities().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("Medicine not found"));

                    // Create a new MedicineWithQuantity and set values
                    MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
                    medicineWithQuantity.setMedicine(medicine);
                    medicineWithQuantity.setQuote(dto.getQuote()); // Set the quantity
                    medicineWithQuantity.setAgentContract(existingContract); // Associate with existing AgentContract

                    return medicineWithQuantity;
                }).collect(Collectors.toList());

        // Set updated medicines and quantities in the existing AgentContract
        existingContract.setMedicinesWithQuantities(updatedMedicinesWithQuantities);

        // Save the updated contract
        agentContractRepository.save(existingContract);

        // Return the updated DTO
        return convertToDTO(existingContract);
    }




}
