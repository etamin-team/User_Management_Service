package com.example.user_management_service.service;

import com.example.user_management_service.exception.AgentContractException;
import com.example.user_management_service.exception.AgentContractExistsException;
import com.example.user_management_service.exception.ContractNotFoundException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final ManagerGoalRepository managerGoalRepository;
    private final MedicineRepository medicineRepository;
    private final DistrictRepository districtRepository;
    private final ContractMedicineAmountRepository contractMedicineAmountRepository;
    private final ContractFieldAmountRepository contractFieldAmountRepository;
    private final ContractDistrictAmountRepository contractDistrictAmountRepository;

    private final AgentContractRepository agentContractRepository;
    private final MedicineWithQuantityRepository medicineWithQuantityRepository;
    private final ContractService contractService;
    private final FieldWithQuantityRepository fieldWithQuantityRepository;
    private final MedicineGoalQuantityRepository medicineGoalQuantityRepository;
    private final DistrictGoalQuantityRepository districtGoalQuantityRepository;
    private final FieldGoalQuantityRepository fieldGoalQuantityRepository;
    private final ContractRepository contractRepository;
    private final RecipeRepository recipeRepository;

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
                user.getDistrict().getId(),
                user.getRole()
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
        if (managerGoalRepository.getGoalsByManagerId(managerGoalDTO.getManagerId()).isPresent()) {
            throw new ContractNotFoundException("Manager has already assigned goalId:" + managerGoalDTO.getManagerId());
        }
        ManagerGoal managerGoal = new ManagerGoal();
        managerGoal.setManagerId(userRepository.findById(managerGoalDTO.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found")));
        managerGoal.setAdmin(userRepository.findById(managerGoalDTO.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin  not found")));
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
                    fieldGoalQuantityRepository.save(fieldGoalQuantity);
                    return fieldGoalQuantity;
                })
                .collect(Collectors.toList()));

        managerGoal.setMedicineGoalQuantities(managerGoalDTO.getMedicineGoalQuantities().stream()
                .map(dto -> {
                    ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                    contractMedicineAmount.setAmount(0L); // Or set it based on your logic
                    contractMedicineAmountRepository.save(contractMedicineAmount);
                    MedicineGoalQuantity medicineGoalQuantity = new MedicineGoalQuantity();
                    medicineGoalQuantity.setQuote(dto.getQuote());
                    medicineGoalQuantity.setManagerGoal(managerGoal);
                    Medicine medicine = medicineRepository.getOne(dto.getMedicineId());
                    medicineGoalQuantity.setMedicine(medicine);
                    medicineGoalQuantity.setContractMedicineAmount(contractMedicineAmount);
                    medicineGoalQuantityRepository.save(medicineGoalQuantity);
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
                    districtGoalQuantityRepository.save(districtGoalQuantity);
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

                    existingGoal.setMedicineGoalQuantities(updateGoalDTO.getMedicineGoalQuantities().stream()
                            .map(dto -> {
                                MedicineGoalQuantity medicineGoalQuantity = new MedicineGoalQuantity();
                                medicineGoalQuantity.setId(dto.getMedicineId());
                                medicineGoalQuantity.setQuote(dto.getQuote());
                                medicineGoalQuantity.setManagerGoal(existingGoal);
                                Medicine medicine = medicineRepository.getOne(dto.getMedicineId());
                                medicineGoalQuantity.setMedicine(medicine);

                                ContractMedicineAmount contractMedicineAmount = dto.getMedicineId() != null
                                        ? contractMedicineAmountRepository.findById(dto.getMedicineId()).orElse(null)
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
        ManagerGoalDTO managerGoalDTO = new ManagerGoalDTO(
                goal.getGoalId(),
                goal.getManagerId().getUserId(),
                goal.getFieldGoalQuantities().stream()
                        .map(q -> new FieldGoalQuantityDTO(q.getId(), q.getField(), q.getQuote(), q.getManagerGoal().getGoalId(), q.getContractFieldAmount()))
                        .collect(Collectors.toList()),
                goal.getMedicineGoalQuantities().stream()
                        .map(q -> new MedicineGoalQuantityDTO(q.getId(), q.getMedicine().getId(), q.getMedicine().getName(), q.getQuote(), q.getManagerGoal().getGoalId(), q.getContractMedicineAmount()))
                        .collect(Collectors.toList()),
                goal.getDistrictGoalQuantities().stream()
                        .map(q -> new DistrictGoalQuantityDTO(q.getId(), q.getDistrict().getId(), q.getDistrict().getName(), q.getQuote(), q.getManagerGoal().getGoalId(), q.getContractDistrictAmount()))
                        .collect(Collectors.toList()),
                goal.getCreatedAt(),
                goal.getStartDate(),
                goal.getEndDate(),
                goal.getAdmin().getUserId()
        );
        return managerGoalDTO;
    }


    public ManagerGoalDTO getManagerGoalById(Long goalId) {
        Optional<ManagerGoal> managerGoalOptional = managerGoalRepository.findById(goalId);
        if (managerGoalOptional.isEmpty()) {
            throw new IllegalStateException("Manager Goal not found");
        }

        ManagerGoal managerGoal = managerGoalOptional.get();

        // Mapping ManagerGoal to ManagerGoalDTO
        ManagerGoalDTO managerGoalDTO = new ManagerGoalDTO();
        managerGoalDTO.setGoalId(managerGoal.getGoalId());
        managerGoalDTO.setCreatedAt(managerGoal.getCreatedAt());
        managerGoalDTO.setStartDate(managerGoal.getStartDate());
        managerGoalDTO.setEndDate(managerGoal.getEndDate());
        managerGoalDTO.setManagerId(managerGoal.getManagerId().getUserId());
        managerGoalDTO.setAdminId(managerGoal.getAdmin().getUserId());
        // Mapping related MedicinesWithQuantities
        @NotNull List<MedicineGoalQuantityDTO> medicineWithQuantityDTOS = managerGoal.getMedicineGoalQuantities().stream()
                .map(med -> new MedicineGoalQuantityDTO(med.getId(), med.getId(), med.getMedicine().getName(),
                        med.getQuote(), med.getManagerGoal().getGoalId(), med.getContractMedicineAmount()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setMedicineGoalQuantities(medicineWithQuantityDTOS);

        // Mapping related FieldsWithQuantities
        List<FieldGoalQuantityDTO> fieldWithQuantityDTOS = managerGoal.getFieldGoalQuantities().stream()
                .map(field -> new FieldGoalQuantityDTO(field.getId(), field.getField(),
                        field.getQuote(), field.getManagerGoal().getGoalId(), field.getContractFieldAmount()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setFieldGoalQuantities(fieldWithQuantityDTOS);

        return managerGoalDTO;
    }

    public ManagerGoalDTO getManagerGoalsByManagerId(UUID managerId) {
        ManagerGoal managerGoal = managerGoalRepository.getGoalsByManagerId(managerId)
                .orElseThrow(() -> new IllegalStateException("No Manager Goals found for managerId: " + managerId));
        ManagerGoalDTO managerGoalDTO = new ManagerGoalDTO();
        managerGoalDTO.setGoalId(managerGoal.getGoalId());
        managerGoalDTO.setCreatedAt(managerGoal.getCreatedAt());
        managerGoalDTO.setStartDate(managerGoal.getStartDate());
        managerGoalDTO.setEndDate(managerGoal.getEndDate());
        managerGoalDTO.setManagerId(managerGoal.getManagerId().getUserId());
        managerGoalDTO.setAdminId(managerGoal.getAdmin().getUserId());

        // Mapping related MedicinesWithQuantities
        List<MedicineGoalQuantityDTO> medicineWithQuantityDTOS = managerGoal.getMedicineGoalQuantities().stream()
                .map(med -> new MedicineGoalQuantityDTO(med.getId(), med.getMedicine().getId(), med.getMedicine().getName(),
                        med.getQuote(), med.getManagerGoal().getGoalId(), med.getContractMedicineAmount()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setMedicineGoalQuantities(medicineWithQuantityDTOS);

        // Mapping related FieldsWithQuantities
        List<FieldGoalQuantityDTO> fieldWithQuantityDTOS = managerGoal.getFieldGoalQuantities().stream()
                .map(field -> new FieldGoalQuantityDTO(field.getId(), field.getField(),
                        field.getQuote(), field.getManagerGoal().getGoalId(), field.getContractFieldAmount()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setFieldGoalQuantities(fieldWithQuantityDTOS);

        List<DistrictGoalQuantityDTO> districtGoalQuantityDTOS = managerGoal.getDistrictGoalQuantities().stream()
                .map(district -> new DistrictGoalQuantityDTO(district.getId(), district.getDistrict().getId(), district.getDistrict().getName(),
                        district.getQuote(), district.getManagerGoal().getGoalId(), district.getContractDistrictAmount()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setDistrictGoalQuantities(districtGoalQuantityDTOS);

        return managerGoalDTO;
    }

    //agent contract

    public AgentContractDTO createAgentContract(AgentContractDTO agentContractDTO) {
        if (agentContractRepository.findByMedAgentUserId(agentContractDTO.getMedAgentId()).isPresent()) {
            throw new AgentContractExistsException("Agent has already assigned contract agentId:" + agentContractDTO.getMedAgentId());
        }
        AgentContract agentContract = new AgentContract();
        agentContract.setCreatedAt(LocalDate.now());
        agentContract.setStartDate(agentContractDTO.getStartDate());
        agentContract.setEndDate(agentContractDTO.getEndDate());
        agentContract.setMedAgent(userRepository.findById(agentContractDTO.getMedAgentId())
                .orElseThrow(() -> new AgentContractException("Manager not found")));
        agentContract.setManager(userRepository.findById(agentContractDTO.getManagerId())
                .orElseThrow(() -> new AgentContractException("Manager not found")));
        ManagerGoal managerGoal = managerGoalRepository.findById(agentContractDTO.getManagerGoalId()).orElseThrow();
        if (agentContractDTO.getManagerGoalId() != null) {
            agentContract.setManagerGoal(managerGoalRepository.findById(agentContractDTO.getManagerGoalId())
                    .orElseThrow(() -> new AgentContractException("Manager Goal not found")));
        }
//        agentContract.setDistrictGoalQuantity(districtGoalQuantityRepository.findById());

        /*** Come back here later to fix the freaking bug */
        agentContract.setManagerGoal(managerGoal);

        agentContractRepository.save(agentContract);

        Set<Long> medicineIds = managerGoal.getMedicineGoalQuantities()
                .stream()
                .map(mq -> mq.getMedicine().getId())
                .collect(Collectors.toSet());

        agentContract.setMedicinesWithQuantities(agentContractDTO.getMedicineWithQuantityDTOS().stream()
                .map(dto -> createMedicineWithQuantity(dto, medicineIds, agentContract))
                .collect(Collectors.toList()));


        Set<Field> fieldIds = managerGoal.getFieldGoalQuantities()
                .stream()
                .map(fq -> fq.getField())
                .collect(Collectors.toSet());

        agentContract.setFieldWithQuantities(agentContractDTO.getFieldWithQuantityDTOS().stream()
                .map(dto -> createFieldWithQuantity(dto, fieldIds, managerGoal, agentContract))
                .collect(Collectors.toList()));


        AgentContract savedContract = agentContractRepository.save(agentContract);
        return convertToDTO(savedContract);
    }

    private MedicineWithQuantity createMedicineWithQuantity(MedicineWithQuantityDTO dto,
                                                            Set<Long> medicineIds,
                                                            AgentContract agentContract) {
        if (!medicineIds.contains(dto.getMedicineId())) {
            throw new AgentContractException("Medicine with ID " + dto.getMedicineId() + " not found in managerGoalQuantities");
        }

        ContractMedicineAmount medicineGoalQuantity = medicineGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(dto.getMedicineId(), agentContract.getManagerGoal().getGoalId())
                .orElseThrow(() -> new AgentContractException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()));

        MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
        medicineWithQuantity.setMedicine(medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new AgentContractException("Medicine not found")));
        medicineWithQuantity.setQuote(dto.getQuote());
        medicineWithQuantity.setContractMedicineAmount(medicineGoalQuantity);

        ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
        contractMedicineAmount.setAmount(0L); // Or set it based on your logic
        contractMedicineAmountRepository.save(contractMedicineAmount);

        medicineWithQuantity.setContractMedicineAmount(contractMedicineAmount);
        medicineWithQuantity.setAgentContract(agentContract);
        medicineWithQuantityRepository.save(medicineWithQuantity);
        return medicineWithQuantity;
    }

    private FieldWithQuantity createFieldWithQuantity(FieldWithQuantityDTO dto,
                                                      Set<Field> fieldIds,
                                                      ManagerGoal managerGoal,
                                                      AgentContract agentContract) {
        if (!fieldIds.contains(dto.getFieldName())) {
            return null;
        }

        ContractFieldAmount contractFieldAmount = fieldGoalQuantityRepository
                .findContractFieldAmountByFieldAndGoalId(dto.getFieldName(), managerGoal.getGoalId())
                .orElseThrow(() -> new AgentContractException("ContractFieldAmount not found for field ID " + dto.getId()));

        FieldWithQuantity fieldWithQuantity = new FieldWithQuantity();
        fieldWithQuantity.setField(dto.getFieldName());
        fieldWithQuantity.setQuote(dto.getQuote());
        fieldWithQuantity.setContractFieldAmount(contractFieldAmount);

        ContractFieldAmount contractFieldMedAgentAmount = new ContractFieldAmount();
        contractFieldMedAgentAmount.setAmount(0L); // Adjust logic as needed
        contractFieldAmountRepository.save(contractFieldMedAgentAmount);

        fieldWithQuantity.setContractFieldMedAgentAmount(contractFieldMedAgentAmount);
        fieldWithQuantity.setAgentContract(agentContract);
        fieldWithQuantityRepository.save(fieldWithQuantity);

        return fieldWithQuantity;
    }


    public AgentContractDTO updateAgentContract(Long contractId, AgentContractDTO agentContractDTO) {
        AgentContract agentContract = agentContractRepository.findById(contractId)
                .orElseThrow(() -> new AgentContractException("AgentContract not found"));

        agentContract.setStartDate(agentContractDTO.getStartDate());
        agentContract.setEndDate(agentContractDTO.getEndDate());

        // Update the MedAgent (you can add more logic here if needed)
        agentContract.setMedAgent(userRepository.findById(agentContractDTO.getMedAgentId())
                .orElseThrow(() -> new AgentContractException("Manager not found")));

        ManagerGoal managerGoal = managerGoalRepository.getById(agentContractDTO.getManagerGoalId());

        // Get existing medicine IDs from ManagerGoal for comparison
        Set<Long> medicineIds = managerGoal.getMedicineGoalQuantities()
                .stream()
                .map(mq -> mq.getMedicine().getId())
                .collect(Collectors.toSet());

        // Update the medicines with quantities, comparing with the existing manager goal medicines
        agentContract.setMedicinesWithQuantities(agentContractDTO.getMedicineWithQuantityDTOS().stream()
                .map(dto -> {
                    if (!medicineIds.contains(dto.getMedicineId())) {
                        throw new AgentContractException("Medicine with ID " + dto.getMedicineId() + " not found in managerGoalQuantities");
                    }

                    // Check if the new quantity is already in the manager goal's quantities
                    boolean isNewQuantity = managerGoal.getMedicineGoalQuantities().stream()
                            .noneMatch(mq -> mq.getMedicine().getId().equals(dto.getMedicineId()));

                    if (isNewQuantity) {
                        // If it's a new quantity, add it and set logic here
                        ContractMedicineAmount medicineGoalQuantity = medicineWithQuantityRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new AgentContractException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()))
                                .getContractMedicineAmount();

                        MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
                        medicineWithQuantity.setMedicine(medicineRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new AgentContractException("Medicine not found")));
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
                                .orElseThrow(() -> new AgentContractException("MedicineWithQuantity not found for medicine ID " + dto.getMedicineId()));

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
                        throw new AgentContractException("Field with name " + dto.getFieldName() + " not found in managerGoalFields");
                    }

                    ContractFieldAmount contractFieldAmount = contractFieldAmountRepository.findById(dto.getId())
                            .orElseThrow(() -> new AgentContractException("ContractFieldAmount not found for field ID " + dto.getId()));

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
                    .orElseThrow(() -> new AgentContractException("Manager Goal not found")));
        }

        AgentContract savedContract = agentContractRepository.save(agentContract);
        return convertToDTO(savedContract);
    }

    public void deleteAgentContract(Long contractId) {
        if (!agentContractRepository.existsById(contractId)) {
            throw new AgentContractException("Agent Contract not found");
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
                                mq.getQuote(),
                                mq.getContractMedicineAmount()
                        )).collect(Collectors.toList()) : List.of(),
                agentContract.getFieldWithQuantities() != null ? agentContract.getFieldWithQuantities().stream()
                        .map(fq -> new FieldWithQuantityDTO(
                                fq.getId(),
                                fq.getField(),
                                fq.getQuote(),
                                fq.getContractFieldAmount()
                        )).collect(Collectors.toList()) : List.of(),
                agentContract.getManagerGoal() != null ? agentContract.getManagerGoal().getGoalId() : null,
                agentContract.getManager().getUserId() != null ? agentContract.getManager().getUserId() : null,
                agentContract.getDistrictGoalQuantity() != null ? agentContract.getDistrictGoalQuantity().getId() : null
        );
    }


    public AgentContractDTO getAgentContractById(Long agentContractId) {
        Optional<AgentContract> agentContractOptional = agentContractRepository.findById(agentContractId);
        if (agentContractOptional.isEmpty()) {
            throw new IllegalStateException("Agent Contract not found");
        }

        AgentContract agentContract = agentContractOptional.get();

        // Mapping AgentContract to AgentContractDTO
        AgentContractDTO agentContractDTO = new AgentContractDTO();
        agentContractDTO.setId(agentContract.getId());
        agentContractDTO.setCreatedAt(agentContract.getCreatedAt());
        agentContractDTO.setStartDate(agentContract.getStartDate());
        agentContractDTO.setEndDate(agentContract.getEndDate());
        agentContractDTO.setMedAgentId(agentContract.getMedAgent().getUserId());
        agentContractDTO.setManagerId(agentContract.getManager().getUserId());
        agentContractDTO.setManagerGoalId(agentContract.getManagerGoal() != null ? agentContract.getManagerGoal().getGoalId() : null);
        agentContractDTO.setDistrictId(agentContract.getDistrictGoalQuantity() != null ? agentContract.getDistrictGoalQuantity().getId() : null);

        // Mapping MedicineWithQuantity to MedicineWithQuantityDTO
        List<MedicineWithQuantityDTO> medicineWithQuantityDTOS = agentContract.getMedicinesWithQuantities().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getId(), med.getMedicine().getName(),
                        med.getQuote(), med.getContractMedicineAmount()))  // mapping to DTO
                .collect(Collectors.toList());

        agentContractDTO.setMedicineWithQuantityDTOS(medicineWithQuantityDTOS);

        // Mapping FieldWithQuantity to FieldWithQuantityDTO
        List<FieldWithQuantityDTO> fieldWithQuantityDTOS = agentContract.getFieldWithQuantities().stream()
                .map(field -> new FieldWithQuantityDTO(field.getId(), field.getField(),
                        field.getQuote(), field.getContractFieldAmount()))  // mapping to DTO
                .collect(Collectors.toList());

        agentContractDTO.setFieldWithQuantityDTOS(fieldWithQuantityDTOS);

        return agentContractDTO;
    }

    public AgentContractDTO getAgentContractByMedAgentId(UUID medAgentId) {
        Optional<AgentContract> agentContractOptional = agentContractRepository.findByMedAgentUserId(medAgentId); // You will need to write this query in the repository
        if (agentContractOptional.isEmpty()) {
            throw new IllegalStateException("Agent Contract not found for medAgentId: " + medAgentId);
        }

        AgentContract agentContract = agentContractOptional.get();

        // Mapping AgentContract to AgentContractDTO
        AgentContractDTO agentContractDTO = new AgentContractDTO();
        agentContractDTO.setId(agentContract.getId());
        agentContractDTO.setCreatedAt(agentContract.getCreatedAt());
        agentContractDTO.setStartDate(agentContract.getStartDate());
        agentContractDTO.setEndDate(agentContract.getEndDate());
        agentContractDTO.setMedAgentId(agentContract.getMedAgent().getUserId());
        agentContractDTO.setManagerId(agentContract.getManager().getUserId());
        agentContractDTO.setManagerGoalId(agentContract.getManagerGoal() != null ? agentContract.getManagerGoal().getGoalId() : null);
        agentContractDTO.setDistrictId(agentContract.getDistrictGoalQuantity() != null ? agentContract.getDistrictGoalQuantity().getId() : null);

        // Mapping MedicineWithQuantity to MedicineWithQuantityDTO
        List<MedicineWithQuantityDTO> medicineWithQuantityDTOS = agentContract.getMedicinesWithQuantities().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getId(), med.getMedicine().getName(),
                        med.getQuote(), med.getContractMedicineAmount()))  // mapping to DTO
                .collect(Collectors.toList());

        agentContractDTO.setMedicineWithQuantityDTOS(medicineWithQuantityDTOS);

        // Mapping FieldWithQuantity to FieldWithQuantityDTO
        List<FieldWithQuantityDTO> fieldWithQuantityDTOS = agentContract.getFieldWithQuantities().stream()
                .map(field -> new FieldWithQuantityDTO(field.getId(), field.getField(),
                        field.getQuote(), field.getContractFieldAmount()))  // mapping to DTO
                .collect(Collectors.toList());

        agentContractDTO.setFieldWithQuantityDTOS(fieldWithQuantityDTOS);

        return agentContractDTO;
    }


    public MedAgentStatusDTO getMedAgentStatusInfo(UUID medAgentId) {
        MedAgentStatusDTO medAgentStatusDTO = new MedAgentStatusDTO();
        medAgentStatusDTO.setAllConnectedDoctors(contractRepository.countDoctorsByMedAgent(medAgentId));
        medAgentStatusDTO.setConnectedDoctorsThisMonth(contractRepository.countDoctorsByMedAgentThisMonth(medAgentId));

        medAgentStatusDTO.setAllConnectedContracts(contractRepository.countContractsByMedAgent(medAgentId));
        medAgentStatusDTO.setConnectedContractsThisMonth(contractRepository.countContractsCreatedThisMonthByMedAgent(medAgentId));

        medAgentStatusDTO.setWrittenRecipesThisMonth(recipeRepository.countRecipesByDoctorsAssignedByMedAgentThisMonth(medAgentId));

        medAgentStatusDTO.setWrittenMedicinesThisMonth(recipeRepository.totalMedicineAmountByMedAgentThisMonth(medAgentId));

        return medAgentStatusDTO;
    }


    // Doctor Contract

    public ContractDTO createContract(ContractDTO contractDTO) {
        return contractService.createContract(contractDTO);
    }

    public ContractDTO updateContract(Long contractId, ContractDTO contractDTO) {

        return contractService.updateContract(contractId, contractDTO);
    }

    public void deleteContract(Long contractId) {
        contractService.deleteContract(contractId);
    }


}
