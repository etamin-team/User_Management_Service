package com.example.user_management_service.service;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public ManagerGoalDTO createManagerGoal(ManagerGoalDTO managerGoalDTO) {
        // Convert DTO to entity
        ManagerGoal managerGoal = new ManagerGoal();
        managerGoal.setManagerId(userRepository.findById(managerGoalDTO.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found")));
        managerGoal.setFields(managerGoalDTO.getFields());
        managerGoal.setMedicines(medicineRepository.findAllById(managerGoalDTO.getMedicineIds()));
        managerGoal.setDistricts(districtRepository.findAllById(managerGoalDTO.getDistrictIds()));
        managerGoal.setCreatedAt(LocalDate.now());
        managerGoal.setStartDate(managerGoalDTO.getStartDate());
        managerGoal.setEndDate(managerGoalDTO.getEndDate());

        // Save entity
        ManagerGoal savedGoal = managerGoalRepository.save(managerGoal);

        // Convert entity back to DTO and return
        return new ManagerGoalDTO(
                savedGoal.getGoalId(),
                savedGoal.getManagerId().getUserId(),
                savedGoal.getFields(),
                savedGoal.getMedicines().stream().map(Medicine::getId).collect(Collectors.toList()),
                savedGoal.getDistricts().stream().map(District::getId).collect(Collectors.toList()),
                savedGoal.getCreatedAt(),
                savedGoal.getStartDate(),
                savedGoal.getEndDate()
        );
    }

    // Update Manager Goal
    public Optional<ManagerGoalDTO> updateManagerGoal(Long id, ManagerGoalDTO updateGoalDTO) {
        return managerGoalRepository.findById(id)
                .map(existingGoal -> {
                    existingGoal.setFields(updateGoalDTO.getFields());
                    existingGoal.setMedicines(medicineRepository.findAllById(updateGoalDTO.getMedicineIds()));
                    existingGoal.setDistricts(districtRepository.findAllById(updateGoalDTO.getDistrictIds()));
                    existingGoal.setStartDate(updateGoalDTO.getStartDate());
                    existingGoal.setEndDate(updateGoalDTO.getEndDate());

                    // Save the updated goal
                    ManagerGoal updatedGoal = managerGoalRepository.save(existingGoal);

                    // Return DTO
                    return new ManagerGoalDTO(
                            updatedGoal.getGoalId(),
                            updatedGoal.getManagerId().getUserId(),
                            updatedGoal.getFields(),
                            updatedGoal.getMedicines().stream().map(Medicine::getId).collect(Collectors.toList()),
                            updatedGoal.getDistricts().stream().map(District::getId).collect(Collectors.toList()),
                            updatedGoal.getCreatedAt(),
                            updatedGoal.getStartDate(),
                            updatedGoal.getEndDate()
                    );
                });
    }

    // Delete Manager Goal
    public boolean deleteManagerGoal(Long id) {
        if (managerGoalRepository.existsById(id)) {
            managerGoalRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Update Goal Status
    public Optional<ManagerGoalDTO> updateGoalStatus(Long id, GoalStatus status) {
        return managerGoalRepository.findById(id)
                .map(existingGoal -> {
                    existingGoal.setStatus(status);
                    ManagerGoal updatedGoal = managerGoalRepository.save(existingGoal);

                    // Return DTO
                    return new ManagerGoalDTO(
                            updatedGoal.getGoalId(),
                            updatedGoal.getManagerId().getUserId(),
                            updatedGoal.getFields(),
                            updatedGoal.getMedicines().stream().map(Medicine::getId).collect(Collectors.toList()),
                            updatedGoal.getDistricts().stream().map(District::getId).collect(Collectors.toList()),
                            updatedGoal.getCreatedAt(),
                            updatedGoal.getStartDate(),
                            updatedGoal.getEndDate()
                    );
                });
    }

    @Transactional
    public List<ManagerGoalWithDetailsDTO> getManagerGoalsWithDetails(GoalStatus status) {
        List<ManagerGoal> managerGoals = managerGoalRepository.findByStatusWithMedicinesAndDistricts(status);

        List<ManagerGoalWithDetailsDTO> goalDetailsList = new ArrayList<>();

        for (ManagerGoal goal : managerGoals) {
            ManagerGoalWithDetailsDTO dto = new ManagerGoalWithDetailsDTO();
            dto.setGoalId(goal.getGoalId());
            dto.setManagerId(goal.getManagerId().getUserId());
            dto.setFields(goal.getFields());
            dto.setCreatedAt(goal.getCreatedAt());
            dto.setStartDate(goal.getStartDate());
            dto.setEndDate(goal.getEndDate());

            // Set medicines
            List<Medicine> medicineDTOs = goal.getMedicines();
            dto.setMedicines(medicineDTOs);

            // Set districts
            List<DistrictDTO> districtDTOs = goal.getDistricts().stream()
                    .map(district ->new DistrictDTO(
                            district.getId(),
                            district.getName(),
                            district.getNameUzCyrillic(),
                            district.getNameUzLatin(),
                            district.getNameRussian(),
                            district.getRegion().getId()
                    ))
                    .collect(Collectors.toList());
            dto.setDistricts(districtDTOs);

            goalDetailsList.add(dto);
        }

        return goalDetailsList;
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
                agentContract.getContractType(),
                agentContract.getContractStatus(),
                agentContract.getTotalAmount(),
                agentContract.getQuota60(),
                agentContract.getQuota75To90(),
                agentContract.getSu(),
                agentContract.getSb(),
                agentContract.getGz(),
                agentContract.getKb(),
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
        // Create a new AgentContract object
        AgentContract agentContract = new AgentContract();

        // Set values using setter methods
        agentContract.setContractType(agentContractDTO.getContractType());
        agentContract.setContractStatus(agentContractDTO.getContractStatus());
        agentContract.setTotalAmount(agentContractDTO.getTotalAmount());
        agentContract.setQuota60(agentContractDTO.getQuota60());
        agentContract.setQuota75To90(agentContractDTO.getQuota75To90());
        agentContract.setSu(agentContractDTO.getSu());
        agentContract.setSb(agentContractDTO.getSb());
        agentContract.setGz(agentContractDTO.getGz());
        agentContract.setKb(agentContractDTO.getKb());
        agentContract.setCreatedAt(LocalDate.now());  // Set current date for creation
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

        // Set updated values using setter methods
        existingContract.setContractType(agentContractDTO.getContractType());
        existingContract.setContractStatus(agentContractDTO.getContractStatus());
        existingContract.setTotalAmount(agentContractDTO.getTotalAmount());
        existingContract.setQuota60(agentContractDTO.getQuota60());
        existingContract.setQuota75To90(agentContractDTO.getQuota75To90());
        existingContract.setSu(agentContractDTO.getSu());
        existingContract.setSb(agentContractDTO.getSb());
        existingContract.setGz(agentContractDTO.getGz());
        existingContract.setKb(agentContractDTO.getKb());
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
