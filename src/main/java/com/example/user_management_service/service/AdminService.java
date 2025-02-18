package com.example.user_management_service.service;

import com.example.user_management_service.exception.*;
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

    private final AgentGoalRepository agentGoalRepository;
    private final MedicineWithQuantityRepository medicineWithQuantityRepository;
    private final ContractService contractService;
    private final FieldWithQuantityRepository fieldWithQuantityRepository;
    private final MedicineGoalQuantityRepository medicineGoalQuantityRepository;
    private final DistrictGoalQuantityRepository districtGoalQuantityRepository;
    private final FieldGoalQuantityRepository fieldGoalQuantityRepository;
    private final ContractRepository contractRepository;
    private final RecipeRepository recipeRepository;
    private final DistrictRegionService districtRegionService;

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
            throw new ManagerGoalException("Manager has already assigned goalId:" + managerGoalDTO.getManagerId());
        }
        ManagerGoal managerGoal = new ManagerGoal();
        managerGoal.setManagerId(userRepository.findById(managerGoalDTO.getManagerId())
                .orElseThrow(() -> new ManagerGoalException("Manager not found")));
        managerGoal.setAdmin(userRepository.findById(managerGoalDTO.getAdminId())
                .orElseThrow(() -> new ManagerGoalException("Admin  not found")));
        managerGoal.setCreatedAt(LocalDate.now());
        managerGoal.setStartDate(managerGoalDTO.getStartDate());
        managerGoal.setEndDate(managerGoalDTO.getEndDate());

        managerGoalRepository.save(managerGoal);

        managerGoal.setFieldGoalQuantities(managerGoalDTO.getFieldGoalQuantities().stream()
                .map(dto -> {
                    ContractFieldAmount contractFieldAmount = new ContractFieldAmount();
                    contractFieldAmount.setAmount(0L);
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
                    contractMedicineAmount.setAmount(0L);
                    contractMedicineAmountRepository.save(contractMedicineAmount);
                    MedicineGoalQuantity medicineGoalQuantity = new MedicineGoalQuantity();
                    medicineGoalQuantity.setQuote(dto.getQuote());
                    medicineGoalQuantity.setManagerGoal(managerGoal);
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId()).orElseThrow(() -> new ManagerGoalException("Medicine not found"));
                    medicineGoalQuantity.setMedicine(medicine);
                    medicineGoalQuantity.setContractMedicineAmount(contractMedicineAmount);
                    medicineGoalQuantityRepository.save(medicineGoalQuantity);
                    return medicineGoalQuantity;
                })
                .collect(Collectors.toList()));

        managerGoal.setDistrictGoalQuantities(managerGoalDTO.getDistrictGoalQuantities().stream()
                .map(dto -> {
                    ContractDistrictAmount contractDistrictAmount = new ContractDistrictAmount();
                    contractDistrictAmount.setAmount(0L);
                    contractDistrictAmountRepository.save(contractDistrictAmount);
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
        return managerGoalRepository.findById(id).map(existingGoal ->
                updateManagerGoal(existingGoal, updateGoalDTO, id)
        );
    }
    private ManagerGoalDTO updateManagerGoal(ManagerGoal existingGoal, ManagerGoalDTO updateGoalDTO, Long id) {
        existingGoal.setStartDate(updateGoalDTO.getStartDate());
        existingGoal.setEndDate(updateGoalDTO.getEndDate());


        List<FieldGoalQuantity> updatedFieldGoals = updateGoalDTO.getFieldGoalQuantities().stream()
                .map(dto -> {
                    FieldGoalQuantity fieldGoalQuantity ;

                    if (dto.getId()==0){
                        fieldGoalQuantity = new FieldGoalQuantity();
                        ContractFieldAmount contractFieldAmount = new ContractFieldAmount();
                        contractFieldAmount.setAmount(0L);
                        contractFieldAmountRepository.save(contractFieldAmount);
                        fieldGoalQuantity.setContractFieldAmount(contractFieldAmount);
                        fieldGoalQuantity.setField(dto.getFieldName());
                    }else {
                        fieldGoalQuantity = fieldGoalQuantityRepository.findById(dto.getId()).orElseThrow(()->new EntityNotFoundException("Field not found with id: "+dto.getId()));
                        if (fieldGoalQuantity.getManagerGoal().getGoalId()!=id) throw new ManagerGoalException("Manager Goal id: "+id+" don't match with field goal id: "+fieldGoalQuantity.getManagerGoal().getGoalId());
                    }
                    if (dto.getQuote()>fieldGoalQuantity.getContractFieldAmount().getAmount()){
                        fieldGoalQuantity.setQuote(dto.getQuote());
                    }
                    fieldGoalQuantity.setManagerGoal(existingGoal);
                    fieldGoalQuantityRepository.save(fieldGoalQuantity);
                    return fieldGoalQuantity;
                }).collect(Collectors.toList());
        existingGoal.setFieldGoalQuantities(updatedFieldGoals);

        List<MedicineGoalQuantity> updatedMedicineGoals = updateGoalDTO.getMedicineGoalQuantities().stream()
                .map(dto -> {
                    MedicineGoalQuantity medicineGoalQuantity;

                    if (dto.getMedicineId() == 0) {
                        medicineGoalQuantity = new MedicineGoalQuantity();
                        ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                        contractMedicineAmount.setAmount(0L);
                        contractMedicineAmountRepository.save(contractMedicineAmount);
                        medicineGoalQuantity.setContractMedicineAmount(contractMedicineAmount);
                    } else {
                        medicineGoalQuantity = medicineGoalQuantityRepository.findById(dto.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Medicine goal not found with id: " + dto.getId()));
                        if (medicineGoalQuantity.getManagerGoal().getGoalId() != id)
                            throw new ManagerGoalException("Manager Goal id: " + id + " doesn't match with medicine goal id: " + medicineGoalQuantity.getManagerGoal().getGoalId());
                    }

                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new ManagerGoalException("Medicine not found with ID: " + dto.getMedicineId()));
                    medicineGoalQuantity.setMedicine(medicine);

                    if (dto.getQuote() > medicineGoalQuantity.getContractMedicineAmount().getAmount()) {
                        medicineGoalQuantity.setQuote(dto.getQuote());
                    }

                    medicineGoalQuantity.setManagerGoal(existingGoal);
                    medicineGoalQuantityRepository.save(medicineGoalQuantity);
                    return medicineGoalQuantity;
                }).collect(Collectors.toList());

        existingGoal.setMedicineGoalQuantities(updatedMedicineGoals);



        List<DistrictGoalQuantity> updatedDistrictGoals = updateGoalDTO.getDistrictGoalQuantities().stream()
                .map(dto -> {
                    DistrictGoalQuantity districtGoalQuantity;

                    if (dto.getDistrictId() == 0) {
                        districtGoalQuantity = new DistrictGoalQuantity();
                        ContractDistrictAmount contractDistrictAmount = new ContractDistrictAmount();
                        contractDistrictAmount.setAmount(0L);
                        contractDistrictAmountRepository.save(contractDistrictAmount);
                        districtGoalQuantity.setContractDistrictAmount(contractDistrictAmount);
                    } else {
                        districtGoalQuantity = districtGoalQuantityRepository.findById(dto.getId())
                                .orElseThrow(() -> new EntityNotFoundException("District goal not found with id: " + dto.getId()));
                        if (districtGoalQuantity.getManagerGoal().getGoalId() != id)
                            throw new ManagerGoalException("Manager Goal id: " + id + " doesn't match with district goal id: " + districtGoalQuantity.getManagerGoal().getGoalId());
                    }

                    districtGoalQuantity.setDistrict(districtRepository.findById(dto.getDistrictId())
                            .orElseThrow(() -> new ManagerGoalException("District not found with ID: " + dto.getDistrictId())));

                    if (dto.getQuote() > districtGoalQuantity.getContractDistrictAmount().getAmount()) {
                        districtGoalQuantity.setQuote(dto.getQuote());
                    }

                    districtGoalQuantity.setManagerGoal(existingGoal);
                    districtGoalQuantityRepository.save(districtGoalQuantity);
                    return districtGoalQuantity;
                }).collect(Collectors.toList());

        existingGoal.setDistrictGoalQuantities(updatedDistrictGoals);

        ManagerGoal updatedGoal = managerGoalRepository.save(existingGoal);
        return convertToDTO(updatedGoal);
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
                        .map(q -> new MedicineGoalQuantityDTO(q.getId(), q.getQuote(), q.getMedicine().getId(), q.getManagerGoal().getGoalId(), q.getContractMedicineAmount(), q.getMedicine()))
                        .collect(Collectors.toList()),
                goal.getDistrictGoalQuantities().stream()
                        .map(q -> new DistrictGoalQuantityDTO(q.getId(), q.getDistrict().getId(), q.getQuote(), q.getManagerGoal().getGoalId(), q.getContractDistrictAmount(), districtRegionService.regionDistrictDTO(q.getDistrict())))
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
            throw new ManagerGoalExistException("Manager Goal not found by goal Id: " + goalId);
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
                .map(med -> new MedicineGoalQuantityDTO(med.getId(),
                        med.getQuote(), med.getMedicine().getId(), med.getManagerGoal().getGoalId(), med.getContractMedicineAmount(), med.getMedicine()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setMedicineGoalQuantities(medicineWithQuantityDTOS);

        // Mapping related FieldsWithQuantities
        List<FieldGoalQuantityDTO> fieldWithQuantityDTOS = managerGoal.getFieldGoalQuantities().stream()
                .map(field -> new FieldGoalQuantityDTO(field.getId(), field.getField(),
                        field.getQuote(), field.getManagerGoal().getGoalId(), field.getContractFieldAmount()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setFieldGoalQuantities(fieldWithQuantityDTOS);

        List<DistrictGoalQuantityDTO> districtGoalQuantityDTOS = managerGoal.getDistrictGoalQuantities().stream()
                .map(district -> new DistrictGoalQuantityDTO(district.getId(), district.getDistrict().getId(),
                        district.getQuote(), district.getManagerGoal().getGoalId(), district.getContractDistrictAmount(), districtRegionService.regionDistrictDTO(district.getDistrict())))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setDistrictGoalQuantities(districtGoalQuantityDTOS);

        return managerGoalDTO;
    }

    public ManagerGoalDTO getManagerGoalsByManagerId(UUID managerId) {
        ManagerGoal managerGoal = managerGoalRepository.getGoalsByManagerId(managerId)
                .orElseThrow(() -> new ManagerGoalException("No Manager Goals found for managerId: " + managerId));
        ManagerGoalDTO managerGoalDTO = new ManagerGoalDTO();
        managerGoalDTO.setGoalId(managerGoal.getGoalId());
        managerGoalDTO.setCreatedAt(managerGoal.getCreatedAt());
        managerGoalDTO.setStartDate(managerGoal.getStartDate());
        managerGoalDTO.setEndDate(managerGoal.getEndDate());
        managerGoalDTO.setManagerId(managerGoal.getManagerId().getUserId());
        managerGoalDTO.setAdminId(managerGoal.getAdmin().getUserId());

        // Mapping related MedicinesWithQuantities
        List<MedicineGoalQuantityDTO> medicineWithQuantityDTOS = managerGoal.getMedicineGoalQuantities().stream()
                .map(med -> new MedicineGoalQuantityDTO(med.getId(),
                        med.getQuote(), med.getMedicine().getId(), med.getManagerGoal().getGoalId(), med.getContractMedicineAmount(), med.getMedicine()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setMedicineGoalQuantities(medicineWithQuantityDTOS);

        // Mapping related FieldsWithQuantities
        List<FieldGoalQuantityDTO> fieldWithQuantityDTOS = managerGoal.getFieldGoalQuantities().stream()
                .map(field -> new FieldGoalQuantityDTO(field.getId(), field.getField(),
                        field.getQuote(), field.getManagerGoal().getGoalId(), field.getContractFieldAmount()))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setFieldGoalQuantities(fieldWithQuantityDTOS);

        List<DistrictGoalQuantityDTO> districtGoalQuantityDTOS = managerGoal.getDistrictGoalQuantities().stream()
                .map(district -> new DistrictGoalQuantityDTO(district.getId(), district.getDistrict().getId(),
                        district.getQuote(), district.getManagerGoal().getGoalId(), district.getContractDistrictAmount(), districtRegionService.regionDistrictDTO(district.getDistrict())))  // mapping to DTO
                .collect(Collectors.toList());
        managerGoalDTO.setDistrictGoalQuantities(districtGoalQuantityDTOS);

        return managerGoalDTO;
    }

    //agent contract

    public AgentContractDTO createAgentContract(AgentContractDTO agentContractDTO) {
        if (agentGoalRepository.getGoalsByMedAgentUserId(agentContractDTO.getMedAgentId()).isPresent()) {
            throw new AgentGoalExistsException("Agent has already assigned contract agentId: " + agentContractDTO.getMedAgentId());
        }
        AgentGoal agentGoal = new AgentGoal();
        agentGoal.setCreatedAt(LocalDate.now());
        agentGoal.setStartDate(agentContractDTO.getStartDate());
        agentGoal.setEndDate(agentContractDTO.getEndDate());
        agentGoal.setMedAgent(userRepository.findById(agentContractDTO.getMedAgentId())
                .orElseThrow(() -> new AgentGoalException("Med Agent not found")));
        agentGoal.setManager(userRepository.findById(agentContractDTO.getManagerId())
                .orElseThrow(() -> new AgentGoalException("Manager not found")));
        ManagerGoal managerGoal = managerGoalRepository.findById(agentContractDTO.getManagerGoalId()).orElseThrow(() -> new AgentGoalException("Manager Goal not found"));

        agentGoal.setManagerGoal(managerGoal);
        Optional<DistrictGoalQuantity> districtGoalQuantity = districtGoalQuantityRepository
                .findByGoalIdAndDistrictId(agentGoal.getManagerGoal().getGoalId(),
                        agentGoal.getMedAgent().getDistrict().getId());
        agentGoal.setDistrictGoalQuantity(districtGoalQuantity.orElseThrow(()-> new AgentGoalException("District Goal not found")));
        agentGoal.setManagerGoal(managerGoal);
        agentGoalRepository.save(agentGoal);

        Set<Long> medicineIds = managerGoal.getMedicineGoalQuantities()
                .stream()
                .map(mq -> mq.getId())
                .collect(Collectors.toSet());

        agentGoal.setMedicinesWithQuantities(agentContractDTO.getMedicineWithQuantityDTOS().stream()
                .map(dto -> createMedicineWithQuantity(dto, medicineIds, agentGoal))
                .collect(Collectors.toList()));


        Set<Field> fieldIds = managerGoal.getFieldGoalQuantities()
                .stream()
                .map(fq -> fq.getField())
                .collect(Collectors.toSet());

        agentGoal.setFieldWithQuantities(agentContractDTO.getFieldWithQuantityDTOS().stream()
                .map(dto -> createFieldWithQuantity(dto, fieldIds, managerGoal, agentGoal))
                .collect(Collectors.toList()));


        AgentGoal savedContract = agentGoalRepository.save(agentGoal);
        return convertToDTO(savedContract);
    }

    private MedicineWithQuantity createMedicineWithQuantity(MedicineWithQuantityDTO dto,
                                                            Set<Long> medicineIds,
                                                            AgentGoal agentGoal) {
        if (!medicineIds.contains(dto.getMedicineId())) {
            throw new AgentGoalException("Medicine with ID " + dto.getMedicineId() + " not found in managerGoalQuantities");
        }

        ContractMedicineAmount medicineGoalQuantity = medicineGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(dto.getMedicineId(), agentGoal.getManagerGoal().getGoalId())
                .orElseThrow(() -> new AgentGoalException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()));

        MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
        medicineWithQuantity.setMedicine(medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new AgentGoalException("Medicine not found")));
        medicineWithQuantity.setQuote(dto.getQuote());
        medicineWithQuantity.setContractMedicineAmount(medicineGoalQuantity);

        ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
        contractMedicineAmount.setAmount(0L); // Or set it based on your logic
        contractMedicineAmountRepository.save(contractMedicineAmount);

        medicineWithQuantity.setContractMedicineAmount(contractMedicineAmount);
        medicineWithQuantity.setAgentGoal(agentGoal);
        medicineWithQuantityRepository.save(medicineWithQuantity);
        return medicineWithQuantity;
    }

    private FieldWithQuantity createFieldWithQuantity(FieldWithQuantityDTO dto,
                                                      Set<Field> fieldIds,
                                                      ManagerGoal managerGoal,
                                                      AgentGoal agentGoal) {
        if (!fieldIds.contains(dto.getFieldName())) {
            throw new AgentGoalExistsException("Field with name " + dto.getFieldName() + " not found in managerGoalFields");
        }

        ContractFieldAmount contractFieldAmount = fieldGoalQuantityRepository
                .findContractFieldAmountByFieldAndGoalId(dto.getFieldName(), managerGoal.getGoalId())
                .orElseThrow(() -> new AgentGoalException("ContractFieldAmount not found for field ID " + dto.getId()));

        FieldWithQuantity fieldWithQuantity = new FieldWithQuantity();
        fieldWithQuantity.setField(dto.getFieldName());
        fieldWithQuantity.setQuote(dto.getQuote());
        fieldWithQuantity.setContractFieldAmount(contractFieldAmount);

        ContractFieldAmount contractFieldMedAgentAmount = new ContractFieldAmount();
        contractFieldMedAgentAmount.setAmount(0L); // Adjust logic as needed
        contractFieldAmountRepository.save(contractFieldMedAgentAmount);

        fieldWithQuantity.setContractFieldMedAgentAmount(contractFieldMedAgentAmount);
        fieldWithQuantity.setAgentGoal(agentGoal);
        fieldWithQuantityRepository.save(fieldWithQuantity);

        return fieldWithQuantity;
    }


    public AgentContractDTO updateAgentContract(Long contractId, AgentContractDTO agentContractDTO) {
        AgentGoal agentGoal = agentGoalRepository.findById(contractId)
                .orElseThrow(() -> new AgentGoalException("AgentGoal not found"));

        agentGoal.setStartDate(agentContractDTO.getStartDate());
        agentGoal.setEndDate(agentContractDTO.getEndDate());

        // Update the MedAgent (you can add more logic here if needed)
        agentGoal.setMedAgent(userRepository.findById(agentContractDTO.getMedAgentId())
                .orElseThrow(() -> new AgentGoalException("Manager not found")));

        ManagerGoal managerGoal = managerGoalRepository.getById(agentContractDTO.getManagerGoalId());

        // Get existing medicine IDs from ManagerGoal for comparison
        Set<Long> medicineIds = managerGoal.getMedicineGoalQuantities()
                .stream()
                .map(mq -> mq.getId())
                .collect(Collectors.toSet());

        // Update the medicines with quantities, comparing with the existing manager goal medicines
        agentGoal.setMedicinesWithQuantities(agentContractDTO.getMedicineWithQuantityDTOS().stream()
                .map(dto -> {
                    if (!medicineIds.contains(dto.getMedicineId())) {
                        throw new AgentGoalException("Medicine with ID " + dto.getMedicineId() + " not found in managerGoalQuantities");
                    }

                    // Check if the new quantity is already in the manager goal's quantities
                    boolean isNewQuantity = managerGoal.getMedicineGoalQuantities().stream()
                            .noneMatch(mq -> mq.getMedicine().getId().equals(dto.getMedicineId()));

                    if (isNewQuantity) {
                        // If it's a new quantity, add it and set logic here
                        ContractMedicineAmount medicineGoalQuantity = medicineWithQuantityRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new AgentGoalException("ContractMedicineAmount not found for medicine ID " + dto.getMedicineId()))
                                .getContractMedicineAmount();

                        MedicineWithQuantity medicineWithQuantity = new MedicineWithQuantity();
                        medicineWithQuantity.setMedicine(medicineRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new AgentGoalException("Medicine not found")));
                        medicineWithQuantity.setQuote(dto.getQuote());
                        medicineWithQuantity.setContractMedicineAmount(medicineGoalQuantity);
                        ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                        contractMedicineAmount.setAmount(0L); // Or set it based on your logic
                        contractMedicineAmountRepository.save(contractMedicineAmount);
                        medicineWithQuantity.setContractMedicineAmount(contractMedicineAmount);
                        medicineWithQuantity.setAgentGoal(agentGoal);
                        return medicineWithQuantity;
                    } else {
                        // If the quantity already exists, update it here
                        MedicineWithQuantity existingMedicineWithQuantity = medicineWithQuantityRepository.findById(dto.getMedicineId())
                                .orElseThrow(() -> new AgentGoalException("MedicineWithQuantity not found for medicine ID " + dto.getMedicineId()));

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

        agentGoal.setFieldWithQuantities(agentContractDTO.getFieldWithQuantityDTOS().stream()
                .map(dto -> {
                    if (!fieldIds.contains(dto.getFieldName())) {
                        throw new AgentGoalException("Field with name " + dto.getFieldName() + " not found in managerGoalFields");
                    }

                    ContractFieldAmount contractFieldAmount = contractFieldAmountRepository.findById(dto.getId())
                            .orElseThrow(() -> new AgentGoalException("ContractFieldAmount not found for field ID " + dto.getId()));

                    FieldWithQuantity fieldWithQuantity = new FieldWithQuantity();
                    fieldWithQuantity.setField(dto.getFieldName());
                    fieldWithQuantity.setQuote(dto.getQuote());
                    fieldWithQuantity.setContractFieldAmount(contractFieldAmount);

                    ContractFieldAmount contractFieldMedAgentAmount = new ContractFieldAmount();
                    contractFieldMedAgentAmount.setAmount(0L); // Adjust logic as needed
                    contractFieldAmountRepository.save(contractFieldMedAgentAmount);
                    fieldWithQuantity.setContractFieldMedAgentAmount(contractFieldMedAgentAmount);

                    fieldWithQuantity.setAgentGoal(agentGoal);
                    return fieldWithQuantity;
                })
                .collect(Collectors.toList()));

        // Update the manager goal if it has changed
        if (agentContractDTO.getManagerGoalId() != null) {
            agentGoal.setManagerGoal(managerGoalRepository.findById(agentContractDTO.getManagerGoalId())
                    .orElseThrow(() -> new AgentGoalException("Manager Goal not found")));
        }

        AgentGoal savedContract = agentGoalRepository.save(agentGoal);
        return convertToDTO(savedContract);
    }

    public void deleteAgentContract(Long contractId) {
        if (!agentGoalRepository.existsById(contractId)) {
            throw new AgentGoalException("Agent Contract not found");
        }
        agentGoalRepository.deleteById(contractId);
    }

    private AgentContractDTO convertToDTO(AgentGoal agentGoal) {
        if (agentGoal == null) {
            return null;
        }

        return new AgentContractDTO(
                agentGoal.getId(),
                agentGoal.getCreatedAt(),
                agentGoal.getStartDate(),
                agentGoal.getEndDate(),
                agentGoal.getMedAgent() != null ? agentGoal.getMedAgent().getUserId() : null,
                agentGoal.getMedicinesWithQuantities() != null ? agentGoal.getMedicinesWithQuantities().stream()
                        .map(mq -> new MedicineWithQuantityDTO(
                                mq.getMedicine().getId(),
                                mq.getQuote(),
                                mq.getAgentGoal().getId(),
                                mq.getContractMedicineAmount(),
                                mq.getMedicine()
                        )).collect(Collectors.toList()) : List.of(),
                agentGoal.getFieldWithQuantities() != null ? agentGoal.getFieldWithQuantities().stream()
                        .map(fq -> new FieldWithQuantityDTO(
                                fq.getId(),
                                fq.getField(),
                                fq.getQuote(),
                                fq.getContractFieldAmount()
                        )).collect(Collectors.toList()) : List.of(),
                agentGoal.getManagerGoal() != null ? agentGoal.getManagerGoal().getGoalId() : null,
                agentGoal.getManager().getUserId() != null ? agentGoal.getManager().getUserId() : null,
                agentGoal.getDistrictGoalQuantity() != null ? agentGoal.getDistrictGoalQuantity().getId() : null
        );
    }


    public AgentContractDTO getAgentGoalById(Long agentGoalId) {
        Optional<AgentGoal> agentContractOptional = agentGoalRepository.findById(agentGoalId);
        if (agentContractOptional.isEmpty()) {
            throw new IllegalStateException("Agent Contract not found");
        }

        AgentGoal agentGoal = agentContractOptional.get();

        // Mapping AgentGoal to AgentContractDTO
        AgentContractDTO agentContractDTO = new AgentContractDTO();
        agentContractDTO.setId(agentGoal.getId());
        agentContractDTO.setCreatedAt(agentGoal.getCreatedAt());
        agentContractDTO.setStartDate(agentGoal.getStartDate());
        agentContractDTO.setEndDate(agentGoal.getEndDate());
        agentContractDTO.setMedAgentId(agentGoal.getMedAgent().getUserId());
        agentContractDTO.setManagerId(agentGoal.getManager().getUserId());
        agentContractDTO.setManagerGoalId(agentGoal.getManagerGoal() != null ? agentGoal.getManagerGoal().getGoalId() : null);
        agentContractDTO.setDistrictId(agentGoal.getDistrictGoalQuantity() != null ? agentGoal.getDistrictGoalQuantity().getId() : null);

        // Mapping MedicineWithQuantity to MedicineWithQuantityDTO
        List<MedicineWithQuantityDTO> medicineWithQuantityDTOS = agentGoal.getMedicinesWithQuantities().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getMedicine().getId(), med.getQuote(), med.getAgentGoal().getId(),
                        med.getContractMedicineAmount(), med.getMedicine()))  // mapping to DTO
                .collect(Collectors.toList());

        agentContractDTO.setMedicineWithQuantityDTOS(medicineWithQuantityDTOS);

        // Mapping FieldWithQuantity to FieldWithQuantityDTO
        List<FieldWithQuantityDTO> fieldWithQuantityDTOS = agentGoal.getFieldWithQuantities().stream()
                .map(field -> new FieldWithQuantityDTO(field.getId(), field.getField(),
                        field.getQuote(), field.getContractFieldAmount()))  // mapping to DTO
                .collect(Collectors.toList());

        agentContractDTO.setFieldWithQuantityDTOS(fieldWithQuantityDTOS);

        return agentContractDTO;
    }

    public AgentContractDTO getAgentGoalByMedAgentId(UUID medAgentId) {
        Optional<AgentGoal> agentContractOptional = agentGoalRepository.getGoalsByMedAgentUserId(medAgentId); // You will need to write this query in the repository
        if (agentContractOptional.isEmpty()) {
            throw new IllegalStateException("Agent Contract not found for medAgentId: " + medAgentId);
        }

        AgentGoal agentGoal = agentContractOptional.get();

        // Mapping AgentGoal to AgentContractDTO
        AgentContractDTO agentContractDTO = new AgentContractDTO();
        agentContractDTO.setId(agentGoal.getId());
        agentContractDTO.setCreatedAt(agentGoal.getCreatedAt());
        agentContractDTO.setStartDate(agentGoal.getStartDate());
        agentContractDTO.setEndDate(agentGoal.getEndDate());
        agentContractDTO.setMedAgentId(agentGoal.getMedAgent().getUserId());
        agentContractDTO.setManagerId(agentGoal.getManager().getUserId());
        agentContractDTO.setManagerGoalId(agentGoal.getManagerGoal() != null ? agentGoal.getManagerGoal().getGoalId() : null);
        agentContractDTO.setDistrictId(agentGoal.getDistrictGoalQuantity() != null ? agentGoal.getDistrictGoalQuantity().getId() : null);

        // Mapping MedicineWithQuantity to MedicineWithQuantityDTO
        List<MedicineWithQuantityDTO> medicineWithQuantityDTOS = agentGoal.getMedicinesWithQuantities().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getMedicine().getId(),
                        med.getQuote(), med.getAgentGoal().getId(), med.getContractMedicineAmount(), med.getMedicine()))  // mapping to DTO
                .collect(Collectors.toList());

        agentContractDTO.setMedicineWithQuantityDTOS(medicineWithQuantityDTOS);

        // Mapping FieldWithQuantity to FieldWithQuantityDTO
        List<FieldWithQuantityDTO> fieldWithQuantityDTOS = agentGoal.getFieldWithQuantities().stream()
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
