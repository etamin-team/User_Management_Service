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
    private MedicineWithQuantityRepository medicineWithQuantityRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private AgentGoalRepository agentGoalRepository;
    private final ContractMedicineAmountRepository contractMedicineAmountRepository;
    private final MedicineGoalQuantityRepository medicineGoalQuantityRepository;
    private final ContractFieldAmountRepository contractFieldAmountRepository;
    private final FieldGoalQuantityRepository fieldGoalQuantityRepository;


    // Doctor Contract

    public ContractDTO managerCreateContract(ContractDTO contractDTO) {
        if (contractRepository.findActiveContractByDoctorId(contractDTO.getDoctorId()).isPresent()) {
            throw new DoctorContractExistsException("Doctor had already assigned contract doctorId:" + contractDTO.getDoctorId());
        }
        ManagerGoal managerGoal = managerGoalRepository.getGoalsByManagerId(contractDTO.getManagerId()).orElseThrow(() -> new DoctorContractException("Manager Goal Doesn't exists"));

        User doctor = userRepository.findById(contractDTO.getDoctorId())
                .orElseThrow(() -> new DoctorContractException("Doctor not found"));
        List<DistrictGoalQuantity> districtGoalQuantities = managerGoal.getDistrictGoalQuantities();
        boolean isDistrictIdMatches = districtGoalQuantities
                .stream()
                .map(dq -> dq.getDistrict().getId())
                .anyMatch(id -> id.equals(doctor.getDistrict().getId()));
        if (managerGoal.getDistrictGoalQuantities() != null && managerGoal.getDistrictGoalQuantities().size() > 0 && !isDistrictIdMatches) {
//            throw new DoctorContractException("DistrictId of Agent Contract   doesn't match with Doctors districtId");
            DistrictGoalQuantity districtGoalQuantity = districtGoalQuantities
                    .stream()
                    .filter(dq -> dq.getDistrict().getId().equals(doctor.getDistrict().getId()))
                    .findFirst().get();


            ContractDistrictAmount contractDistrictAmount = districtGoalQuantity.getContractDistrictAmount();
            contractDistrictAmount.setAmount(contractDistrictAmount.getAmount() + 1);
            contractDistrictAmountRepository.save(contractDistrictAmount);
        }

        List<FieldGoalQuantity> fieldGoalQuantities = managerGoal.getFieldGoalQuantities();
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


        //Contract
        Contract contract = new Contract();
        contract.setDoctor(doctor);
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());
        contract.setCreatedAt(LocalDate.now());
        contract.setStatus(GoalStatus.APPROVED);
        contract.setManager(managerGoal.getManagerId());
        contract.setContractType(contract.getContractType());
        contractRepository.save(contract);

        List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contractDTO.getMedicinesWithQuantities().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new DoctorContractException("Medicine not found"));

                    MedicineWithQuantityDoctor medicineWithQuantityDoctor = new MedicineWithQuantityDoctor();
                    medicineWithQuantityDoctor.setMedicine(medicine);
                    medicineWithQuantityDoctor.setQuote(dto.getQuote());
                    medicineWithQuantityDoctor.setCorrection(dto.getQuote());
                    medicineWithQuantityDoctor.setDoctorContract(contract);

                    ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                    contractMedicineAmount.setAmount(0L);
                    contractMedicineAmountRepository.save(contractMedicineAmount);
                    medicineWithQuantityDoctor.setContractMedicineDoctorAmount(contractMedicineAmount);
                    ContractMedicineAmount medicineGoalQuantity = medicineGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(medicine.getId(), managerGoal.getGoalId())
                            .orElse(null);
                    if (medicineGoalQuantity != null) {
                        medicineWithQuantityDoctor.setContractMedicineAmount(medicineGoalQuantity);
                        contractMedicineAmountRepository.save(medicineGoalQuantity);
                    }
                    medicineWithQuantityDoctorRepository.save(medicineWithQuantityDoctor);
                    return medicineWithQuantityDoctor;
                })
                .collect(Collectors.toList());

        contract.setMedicineWithQuantityDoctors(medicineWithQuantityDoctors);
        Contract savedContract = contractRepository.save(contract);
        return convertToDTO(savedContract);
    }


    public ContractDTO medAgentCreateContractIfGoalExists(ContractDTO contractDTO) {
        if (contractRepository.findActiveOrPendingContractByDoctorId(contractDTO.getDoctorId()).isPresent()) {
            throw new DoctorContractExistsException("Doctor had already assigned contract doctorId:" + contractDTO.getDoctorId());
        }
        // Fetch the agent contract based on the agentId
        AgentGoal agentGoal = agentGoalRepository.findById(contractDTO.getAgentContractId())
                .orElseThrow(() -> new AgentGoalExistsException("AgentGoal not found"));
        ManagerGoal managerGoal = agentGoal.getManagerGoal();
        if (managerGoal == null) {
            throw new DoctorContractException("AgentGoal does not have an associated ManagerGoal.");
        }
        // Fetch the doctor based on doctorId
        User doctor = userRepository.findById(contractDTO.getDoctorId())
                .orElseThrow(() -> new DoctorContractException("Doctor not found"));
        if (agentGoal.getDistrictGoalQuantity() != null && agentGoal.getDistrictGoalQuantity().getDistrict() != null && doctor.getDistrict().getId() == agentGoal.getDistrictGoalQuantity().getDistrict().getId()) {
            ContractDistrictAmount contractDistrictAmount = agentGoal.getDistrictGoalQuantity().getContractDistrictAmount();
            contractDistrictAmount.setAmount(contractDistrictAmount.getAmount() + 1);
            contractDistrictAmountRepository.save(contractDistrictAmount);
        }

        // Fetch fields from ManagerGoal

        List<FieldGoalQuantity> fieldGoalQuantities = managerGoal.getFieldGoalQuantities();
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

            List<FieldWithQuantity> fieldWithQuantities = agentGoal.getFieldWithQuantities();


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

                for (FieldWithQuantity fieldWithQuantity : fieldWithQuantities) {
                    if (fieldWithQuantity.getField().equals(doctor.getFieldName())) {
                        ContractFieldAmount medAgentAmount = fieldWithQuantity.getContractFieldMedAgentAmount();
                        medAgentAmount.setAmount(medAgentAmount.getAmount() + 1);
                    }
                }

            } else {

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


            agentGoalRepository.save(agentGoal);
        }
        // Create a new Contract instance
        Contract contract = new Contract();
        contract.setDoctor(doctor);
        contract.setAgentGoal(agentGoal);
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());
        contract.setCreatedAt(LocalDate.now());
        contract.setMedAgent(agentGoal.getMedAgent());
        contract.setManager(managerGoal.getManagerId());
        contractRepository.save(contract);
        List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contractDTO.getMedicinesWithQuantities().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new DoctorContractException("Medicine not found"));

                    MedicineWithQuantityDoctor medicineWithQuantityDoctor = new MedicineWithQuantityDoctor();
                    medicineWithQuantityDoctor.setMedicine(medicine);
                    medicineWithQuantityDoctor.setQuote(dto.getQuote());
                    medicineWithQuantityDoctor.setCorrection(dto.getQuote());
                    medicineWithQuantityDoctor.setDoctorContract(contract);

                    ContractMedicineAmount contractMedicineAmount = new ContractMedicineAmount();
                    contractMedicineAmount.setAmount(0L);
                    contractMedicineAmountRepository.save(contractMedicineAmount);
                    medicineWithQuantityDoctor.setContractMedicineDoctorAmount(contractMedicineAmount);

                    ContractMedicineAmount medicineGoalQuantity = medicineGoalQuantityRepository.findContractMedicineAmountByMedicineIdAndGoalId(medicine.getId(), agentGoal.getManagerGoal().getGoalId())
                            .orElse(null);
                    if (medicineGoalQuantity != null) {
                        medicineWithQuantityDoctor.setContractMedicineAmount(medicineGoalQuantity);
                        contractMedicineAmountRepository.save(medicineGoalQuantity);
                    }

                    ContractMedicineAmount contractMedicineMedAgentAmount = medicineWithQuantityRepository
                            .findContractMedicineAmountByMedicineIdAndContractId(dto.getMedicineId(), agentGoal.getId()).orElse(null);
                    if (contractMedicineMedAgentAmount != null) {
                        medicineWithQuantityDoctor.setContractMedicineMedAgentAmount(contractMedicineMedAgentAmount);
                        contractMedicineAmountRepository.save(contractMedicineMedAgentAmount);

                    }
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
                .orElseThrow(() -> new DoctorContractException("Contract not found"));

        // Update contract details
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());

        // Update or add medicines with quantities
        contract.setMedicineWithQuantityDoctors(contractDTO.getMedicinesWithQuantities().stream()
                .map(dto -> {
                    Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                            .orElseThrow(() -> new DoctorContractException("Medicine not found"));

                    MedicineWithQuantityDoctor existingMedicineWithQuantityDoctor = contract.getMedicineWithQuantityDoctors().stream()
                            .filter(m -> m.getMedicine().getId().equals(dto.getMedicineId()))
                            .findFirst()
                            .orElse(null);

                    if (existingMedicineWithQuantityDoctor != null) {
                        // Update existing medicine quantity
                        if (dto.getQuote() >= existingMedicineWithQuantityDoctor.getQuote() || dto.getQuote() >= dto.getContractMedicineAmount().getAmount()) {
                            existingMedicineWithQuantityDoctor.setQuote(dto.getQuote());
                            existingMedicineWithQuantityDoctor.setCorrection(dto.getQuote());

                            return existingMedicineWithQuantityDoctor;
                        } else {
                            throw new DoctorContractException("Contract Medicine Quote does not match Contract Medicine Quote");
                        }

                    } else {
                        // Add new medicine with quantity
                        MedicineWithQuantityDoctor newMedicineWithQuantityDoctor = new MedicineWithQuantityDoctor();
                        newMedicineWithQuantityDoctor.setMedicine(medicine);
                        newMedicineWithQuantityDoctor.setQuote(dto.getQuote());
                        newMedicineWithQuantityDoctor.setCorrection(dto.getQuote());
                        newMedicineWithQuantityDoctor.setDoctorContract(contract);
                        return newMedicineWithQuantityDoctor;
                    }
                })
                .collect(Collectors.toList()));


        Contract savedContract = contractRepository.save(contract);


        return convertToDTO(savedContract);
    }

    // Delete a Contract
    public void deleteContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new DoctorContractException("Contract not found"));

        contractRepository.delete(contract);
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
                            .map(medicineWithQuantity -> new MedicineWithQuantityDTO(
                                    medicineWithQuantity.getId(),
                                    medicineWithQuantity.getMedicine() != null ? medicineWithQuantity.getMedicine().getId() : null,
                                    medicineWithQuantity.getQuote(),
                                    medicineWithQuantity.getCorrection(),
                                    medicineWithQuantity.getDoctorContract().getAgentGoal() != null ? medicineWithQuantity.getDoctorContract().getAgentGoal().getId() : null,
                                    medicineWithQuantity.getContractMedicineDoctorAmount(),
                                    medicineWithQuantity.getMedicine()
                            ))
                            .collect(Collectors.toList())
                            : Collections.emptyList(),
                    districtRegionService.regionDistrictDTO(contract.getDoctor().getDistrict()),
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

        if (contract == null) {
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
                            .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + medicineId));

                    OutOfContractMedicineAmount newOutOfContractMedicine = new OutOfContractMedicineAmount();
                    newOutOfContractMedicine.setAmount(1L);
                    newOutOfContractMedicine.setDoctor(userRepository.findById(doctorId).orElseThrow(() -> new ContractNotFoundException("Doctor not found with ID: " + doctorId)));
                    newOutOfContractMedicine.setCreatedAt(LocalDate.now());
                    newOutOfContractMedicine.setMedicine(medicine);

                    outOfContractMedicineAmountRepository.save(newOutOfContractMedicine);
                }
            }
        } else {

            List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors = contract.getMedicineWithQuantityDoctors();

            if (medicineWithQuantityDoctors == null || medicineWithQuantityDoctors.isEmpty()) {
                throw new DoctorContractException("No medicines found for the contract of doctor ID: " + doctorId);
            }

            // Convert existing medicines to a map for quick lookup
            Map<Long, MedicineWithQuantityDoctor> medicineMap = medicineWithQuantityDoctors.stream()
                    .collect(Collectors.toMap(m -> m.getMedicine().getId(), m -> m));

            // Fetch existing out-of-contract medicines

            for (Long medicineId : medicineIds) {
                MedicineWithQuantityDoctor medicineEntry = medicineMap.get(medicineId);

                if (medicineEntry != null && medicineEntry.getQuote() > medicineEntry.getContractMedicineDoctorAmount().getAmount()) {
                    // Medicine exists in contract, update amounts
                    ContractMedicineAmount doctorAmount = medicineEntry.getContractMedicineDoctorAmount();
                    ContractMedicineAmount managerAmount = medicineEntry.getContractMedicineAmount();
                    managerAmount.setAmount(managerAmount.getAmount() + 1);
                    doctorAmount.setAmount(doctorAmount.getAmount() + 1);
                    if (medicineEntry.getContractMedicineMedAgentAmount() != null) {
                        ContractMedicineAmount medAgentAmount = medicineEntry.getContractMedicineMedAgentAmount();
                        medAgentAmount.setAmount(medAgentAmount.getAmount() + 1);
                        contractMedicineAmountRepository.save(medAgentAmount);
                    }
                    contractMedicineAmountRepository.save(managerAmount);


                    contractMedicineAmountRepository.save(doctorAmount);
                } else {
                    // Check if the medicine is already in out-of-contract list
                    Optional<OutOfContractMedicineAmount> existingMedicine = outOfContractMedicines.stream()
                            .filter(m -> m.getMedicine().getId().equals(medicineId))
                            .findFirst();

                    if (existingMedicine.isPresent()) {
                        // If medicine exists, increment the amount
                        OutOfContractMedicineAmount outOfContractMedicine = existingMedicine.get();
                        outOfContractMedicine.setAmount(outOfContractMedicine.getAmount() + 1);
                        outOfContractMedicineAmountRepository.save(outOfContractMedicine);
                    } else {
                        // If medicine does not exist, create a new entry
                        Medicine medicine = medicineRepository.findById(medicineId)
                                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + medicineId));

                        OutOfContractMedicineAmount newOutOfContractMedicine = new OutOfContractMedicineAmount();
                        newOutOfContractMedicine.setAmount(1L);
                        newOutOfContractMedicine.setDoctor(contract.getDoctor());
                        newOutOfContractMedicine.setCreatedAt(LocalDate.now());
                        newOutOfContractMedicine.setMedicine(medicine);

                        outOfContractMedicineAmountRepository.save(newOutOfContractMedicine);
                    }
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
        contractDTO.setEndDate(contract.getEndDate());
        contractDTO.setAgentId(contract.getAgentGoal() != null ? contract.getAgentGoal().getId() : null);

        // Mapping contracted medicines (MedicineWithQuantityDTO)
        List<MedicineWithQuantityDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getId(),med.getMedicine().getId(), med.getQuote(), med.getCorrection(), med.getDoctorContract().getAgentGoal().getId(), med.getContractMedicineAmount(), med.getMedicine())) // mapping to DTO
                .collect(Collectors.toList());

        contractDTO.setContractedMedicineWithQuantity(contractedMedicineWithQuantity);
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
        contractDTO.setEndDate(contract.getEndDate());
        contractDTO.setAgentId(contract.getAgentGoal() != null ? contract.getAgentGoal().getId() : null);

        List<MedicineWithQuantityDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getId(),med.getMedicine().getId(),
                        med.getQuote(), med.getCorrection(), med.getDoctorContract().getAgentGoal().getId(), med.getContractMedicineAmount(), med.getMedicine())) // mapping to DTO
                .collect(Collectors.toList());

        contractDTO.setContractedMedicineWithQuantity(contractedMedicineWithQuantity);


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
        contractDTO.setAgentId(contract.getAgentGoal() != null ? contract.getAgentGoal().getId() : null);

        List<MedicineWithQuantityDTO> contractedMedicineWithQuantity = contract.getMedicineWithQuantityDoctors().stream()
                .map(med -> new MedicineWithQuantityDTO(med.getId(),med.getMedicine().getId(),
                        med.getQuote(), med.getCorrection(),med.getDoctorContract().getAgentGoal()!=null?med.getDoctorContract().getAgentGoal().getId():null, med.getContractMedicineAmount(), med.getMedicine())) // mapping to DTO
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

    public List<ContractDTO> getContractsByMedAgent(UUID medAgentId) {
        List<Contract> contracts = contractRepository.findAllByMedAgentId(medAgentId);
        return contracts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public Page<ContractDTO> getFilteredContracts(Long regionId, Long districtId, Long workPlaceId,
                                                  String firstName, String lastName, String middleName,
                                                  Field fieldName, LocalDate startDate,
                                                  LocalDate endDate, Long medicineId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Contract> contractPage = contractRepository.findContracts(regionId, districtId, workPlaceId,
                firstName, lastName, middleName,
                fieldName, startDate, endDate,
                medicineId, pageable);

        // Convert each Contract entity to DTO and maintain pagination
        return contractPage.map(this::convertToDTO);
    }

}
