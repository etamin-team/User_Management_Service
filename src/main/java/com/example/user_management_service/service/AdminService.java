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
import java.util.*;
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
    private final FieldWithQuantityRepository fieldWithQuantityRepository;

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
                user.getWorkplace() == null ? null : user.getWorkplace().getId(),
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
                    Medicine medicine = medicineRepository.getOne(dto.getMedicineId());
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

    @Transactional
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

                                ContractFieldAmount contractFieldAmount = dto.getId() != null
                                        ? contractFieldAmountRepository.findById(dto.getId()).orElse(null)
                                        : null;
                                if (contractFieldAmount == null) {
                                    contractFieldAmount = new ContractFieldAmount();
                                    contractFieldAmount.setAmount(0L);
                                    contractFieldAmountRepository.save(contractFieldAmount);
                                }
                                fieldGoalQuantity.setContractFieldAmount(contractFieldAmount);
                                return fieldGoalQuantity;
                            })
                            .collect(Collectors.toList()));

                    existingGoal.setManagerGoalQuantities(updateGoalDTO.getManagerGoalQuantities().stream()
                            .map(dto -> {
                                MedicineGoalQuantity medicineGoalQuantity = new MedicineGoalQuantity();
                                medicineGoalQuantity.setId(dto.getManagerGoalId());
                                medicineGoalQuantity.setQuote(dto.getQuote());
                                medicineGoalQuantity.setManagerGoal(existingGoal);
                                Medicine medicine = medicineRepository.getOne(dto.getMedicineId());
                                medicineGoalQuantity.setMedicine(medicine);

                                ContractMedicineAmount contractMedicineAmount = dto.getId() != null
                                        ? contractMedicineAmountRepository.findById(dto.getId()).orElse(null)
                                        : null;
                                if (contractMedicineAmount == null) {
                                    contractMedicineAmount = new ContractMedicineAmount();
                                    contractMedicineAmount.setAmount(0L);
                                    contractMedicineAmountRepository.save(contractMedicineAmount);
                                }
                                medicineGoalQuantity.setContractMedicineAmount(contractMedicineAmount);
                                return medicineGoalQuantity;
                            })
                            .collect(Collectors.toList()));

                    existingGoal.setDistrictGoalQuantities(updateGoalDTO.getDistrictGoalQuantities().stream()
                            .map(dto -> {
                                DistrictGoalQuantity districtGoalQuantity = new DistrictGoalQuantity();
                                districtGoalQuantity.setDistrict(districtRepository.getById(dto.getDistrictId()));
                                districtGoalQuantity.setQuote(dto.getQuote());
                                districtGoalQuantity.setManagerGoal(existingGoal);

                                ContractDistrictAmount contractDistrictAmount = dto.getId() != null
                                        ? contractDistrictAmountRepository.findById(dto.getId()).orElse(null)
                                        : null;
                                if (contractDistrictAmount == null) {
                                    contractDistrictAmount = new ContractDistrictAmount();
                                    contractDistrictAmount.setAmount(0L);
                                    contractDistrictAmountRepository.save(contractDistrictAmount);
                                }
                                districtGoalQuantity.setContractDistrictAmount(contractDistrictAmount);
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
                        .map(q -> new FieldGoalQuantityDTO(q.getId(), q.getField(), q.getQuote(), q.getManagerGoal().getGoalId()))
                        .collect(Collectors.toList()),
                goal.getManagerGoalQuantities().stream()
                        .map(q -> new ManagerGoalQuantityDTO(q.getId(), q.getMedicine().getId(), q.getMedicine().getName(), q.getQuote(), q.getManagerGoal().getGoalId()

                        ))
                        .collect(Collectors.toList()),
                goal.getDistrictGoalQuantities().stream()
                        .map(q -> new DistrictGoalQuantityDTO(q.getId(), q.getDistrict().getId(), q.getDistrict().getName(), q.getQuote(), q.getManagerGoal().getGoalId()))
                        .collect(Collectors.toList()),
                goal.getCreatedAt(),
                goal.getStartDate(),
                goal.getEndDate()
        );
    }








    //agent contract

    public AgentContractDTO createAgentContract(AgentContractDTO agentContractDTO) {
        AgentContract agentContract = new AgentContract();
        agentContract.setCreatedAt(LocalDate.now());
        agentContract.setStartDate(agentContractDTO.getStartDate());
        agentContract.setEndDate(agentContractDTO.getEndDate());
        agentContract.setMedAgent(userRepository.findById(agentContractDTO.getMedAgentId())
                .orElseThrow(() -> new RuntimeException("Manager not found")));
        ManagerGoal managerGoal=managerGoalRepository.getById(agentContractDTO.getManagerGoalId());
        Set<Long> medicineIds = managerGoal.getManagerGoalQuantities()
                .stream()
                .map(mq -> mq.getMedicine().getId())
                .collect(Collectors.toSet());

        agentContract.setMedicinesWithQuantities(agentContractDTO.getMedicineWithQuantityDTOS().stream()
                .map(dto -> {
                    if (!medicineIds.contains(dto.getMedicineId())) {
                        throw new RuntimeException("Medicine with ID " + dto.getMedicineId() + " not found in managerGoalQuantities");
                    }

                    ContractMedicineAmount medicineGoalQuantity = medicineWithQuantityRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()))
                            .getContractMedicineAmount();

                    MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
                    medicineWithQuantity.setMedicine(medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("Medicine not found")));
                    medicineWithQuantity.setQuote(dto.getQuote());
                    medicineWithQuantity.setContractMedicineAmount(medicineGoalQuantity);
                    ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                    contractMedicineAmount.setAmount(0L); // Or set it based on your logic
                    contractMedicineAmountRepository.save(contractMedicineAmount);
                    medicineWithQuantity.setContractMedicineAmount(contractMedicineAmount);
                    medicineWithQuantity.setAgentContract(agentContract);
                    return medicineWithQuantity;
                })
                .collect(Collectors.toList()));

        Set<Field> fieldIds = managerGoal.getFieldGoalQuantities()
                .stream()
                .map(fq -> fq.getField())
                .collect(Collectors.toSet());

        agentContract.setFieldWithQuantities(agentContractDTO.getFieldWithQuantityDTOS().stream()
                .map(dto -> {
                    if (!fieldIds.contains(dto.getFieldName())) {
                        throw new RuntimeException("Field with name " + dto.getFieldName() + " not found in managerGoalFields");
                    }

                    ContractFieldAmount contractFieldAmount = contractFieldAmountRepository.findById(dto.getId())
                            .orElseThrow(() -> new RuntimeException("ContractFieldAmount not found for field ID " + dto.getId()));

                    FieldWithQuantity fieldWithQuantity = new FieldWithQuantity();
                    fieldWithQuantity.setField(dto.getFieldName());
                    fieldWithQuantity.setQuote(dto.getQuote());
                    fieldWithQuantity.setContractFieldAmount(contractFieldAmount);

                    ContractFieldAmount contractFieldMedAgentAmount = new ContractFieldAmount();
                    contractFieldMedAgentAmount.setAmount(0L); // Adjust logic as needed
                    contractFieldAmountRepository.save(contractFieldMedAgentAmount);
                    fieldWithQuantity.setContractFieldMedAgentAmount(contractFieldMedAgentAmount);

                    fieldWithQuantity.setAgentContract(agentContract);
                    return fieldWithQuantity;
                })
                .collect(Collectors.toList()));

        if (agentContractDTO.getManagerGoalId() != null) {
            agentContract.setManagerGoal(managerGoalRepository.findById(agentContractDTO.getManagerGoalId())
                    .orElseThrow(() -> new RuntimeException("Manager Goal not found")));
        }

        AgentContract savedContract = agentContractRepository.save(agentContract);
        return convertToDTO(savedContract);
    }

    public AgentContractDTO updateAgentContract(Long contractId, AgentContractDTO agentContractDTO) {
        AgentContract agentContract = agentContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("AgentContract not found"));

        agentContract.setStartDate(agentContractDTO.getStartDate());
        agentContract.setEndDate(agentContractDTO.getEndDate());

        // Update the MedAgent (you can add more logic here if needed)
        agentContract.setMedAgent(userRepository.findById(agentContractDTO.getMedAgentId())
                .orElseThrow(() -> new RuntimeException("Manager not found")));

        ManagerGoal managerGoal = managerGoalRepository.getById(agentContractDTO.getManagerGoalId());

        // Get existing medicine IDs from ManagerGoal for comparison
        Set<Long> medicineIds = managerGoal.getManagerGoalQuantities()
                .stream()
                .map(mq -> mq.getMedicine().getId())
                .collect(Collectors.toSet());

        // Update the medicines with quantities, comparing with the existing manager goal medicines
        agentContract.setMedicinesWithQuantities(agentContractDTO.getMedicineWithQuantityDTOS().stream()
                .map(dto -> {
                    if (!medicineIds.contains(dto.getMedicineId())) {
                        throw new RuntimeException("Medicine with ID " + dto.getMedicineId() + " not found in managerGoalQuantities");
                    }

                    // Check if the new quantity is already in the manager goal's quantities
                    boolean isNewQuantity = managerGoal.getManagerGoalQuantities().stream()
                            .noneMatch(mq -> mq.getMedicine().getId().equals(dto.getMedicineId()));

                    if (isNewQuantity) {
                        // If it's a new quantity, add it and set logic here
                        ContractMedicineAmount medicineGoalQuantity = medicineWithQuantityRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new RuntimeException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()))
                                .getContractMedicineAmount();

                        MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
                        medicineWithQuantity.setMedicine(medicineRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new RuntimeException("Medicine not found")));
                        medicineWithQuantity.setQuote(dto.getQuote());
                        medicineWithQuantity.setContractMedicineAmount(medicineGoalQuantity);
                        ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                        contractMedicineAmount.setAmount(0L); // Or set it based on your logic
                        contractMedicineAmountRepository.save(contractMedicineAmount);
                        medicineWithQuantity.setContractMedicineAmount(contractMedicineAmount);
                        medicineWithQuantity.setAgentContract(agentContract);
                        return medicineWithQuantity;
                    } else {
                        // If the quantity already exists, update it here
                        MedicineWithQuantity existingMedicineWithQuantity = medicineWithQuantityRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new RuntimeException("MedicineWithQuantity not found for medicine ID " + dto.getMedicineId()));

                        existingMedicineWithQuantity.setQuote(dto.getQuote());
                        // Update other fields if necessary
                        return existingMedicineWithQuantity;
                    }
                })
                .collect(Collectors.toList()));

        // Update the field quantities, comparing with the existing manager goal fields
        Set<Field> fieldIds = managerGoal.getFieldGoalQuantities()
                .stream()
                .map(fq -> fq.getField())
                .collect(Collectors.toSet());

        agentContract.setFieldWithQuantities(agentContractDTO.getFieldWithQuantityDTOS().stream()
                .map(dto -> {
                    if (!fieldIds.contains(dto.getFieldName())) {
                        throw new RuntimeException("Field with name " + dto.getFieldName() + " not found in managerGoalFields");
                    }

                    ContractFieldAmount contractFieldAmount = contractFieldAmountRepository.findById(dto.getId())
                            .orElseThrow(() -> new RuntimeException("ContractFieldAmount not found for field ID " + dto.getId()));

                    FieldWithQuantity fieldWithQuantity = new FieldWithQuantity();
                    fieldWithQuantity.setField(dto.getFieldName());
                    fieldWithQuantity.setQuote(dto.getQuote());
                    fieldWithQuantity.setContractFieldAmount(contractFieldAmount);

                    ContractFieldAmount contractFieldMedAgentAmount = new ContractFieldAmount();
                    contractFieldMedAgentAmount.setAmount(0L); // Adjust logic as needed
                    contractFieldAmountRepository.save(contractFieldMedAgentAmount);
                    fieldWithQuantity.setContractFieldMedAgentAmount(contractFieldMedAgentAmount);

                    fieldWithQuantity.setAgentContract(agentContract);
                    return fieldWithQuantity;
                })
                .collect(Collectors.toList()));

        // Update the manager goal if it has changed
        if (agentContractDTO.getManagerGoalId() != null) {
            agentContract.setManagerGoal(managerGoalRepository.findById(agentContractDTO.getManagerGoalId())
                    .orElseThrow(() -> new RuntimeException("Manager Goal not found")));
        }

        AgentContract savedContract = agentContractRepository.save(agentContract);
        return convertToDTO(savedContract);
    }

    public void deleteAgentContract(Long contractId) {
        if (!agentContractRepository.existsById(contractId)) {
            throw new RuntimeException("Agent Contract not found");
        }
        agentContractRepository.deleteById(contractId);
    }

    private AgentContractDTO convertToDTO(AgentContract agentContract) {
        if (agentContract == null) {
            return null;
        }

        return new AgentContractDTO(
                agentContract.getId(),
                agentContract.getCreatedAt(),
                agentContract.getStartDate(),
                agentContract.getEndDate(),
                agentContract.getMedAgent() != null ? agentContract.getMedAgent().getUserId() : null,
                agentContract.getMedicinesWithQuantities() != null ? agentContract.getMedicinesWithQuantities().stream()
                        .map(mq -> new MedicineWithQuantityDTO(
                                mq.getMedicine().getId(),
                                mq.getMedicine().getName(),
                                mq.getQuote()
                        )).collect(Collectors.toList()) : List.of(),
                agentContract.getFieldWithQuantities() != null ? agentContract.getFieldWithQuantities().stream()
                        .map(fq -> new FieldWithQuantityDTO(
                                fq.getId(),
                                fq.getField(), // `Field` is correctly mapped
                                fq.getQuote()
                        )).collect(Collectors.toList()) : List.of(),
                agentContract.getManagerGoal() != null ? agentContract.getManagerGoal().getGoalId() : null,
                agentContract.getDistrictGoalQuantity() != null ? agentContract.getDistrictGoalQuantity().getId() : null
        );
    }



}
