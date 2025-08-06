package com.example.user_management_service.service;

import com.example.user_management_service.exception.BulkSaveException;
import com.example.user_management_service.exception.DataBaseException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.role.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-11:51 AM (GMT+5)
 */
@Service
public class DataBaseService {
    private final ContractRepository contractRepository;
    private final MedicineRepository medicineRepository;
    private final WorkPlaceRepository workPlaceRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DistrictRegionService districtRegionService;
    private final DistrictRepository districtRepository;
    private final MNNRepository mnnRepository;


    @Autowired
    private MedicineAgentGoalQuantityRepository medicineAgentGoalQuantityRepository;

    @Autowired
    private MedicineManagerGoalQuantityRepository medicineManagerGoalQuantityRepository;

    @Autowired
    private MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;

    @Autowired
    private OutOfContractMedicineAmountRepository outOfContractMedicineAmountRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private SalesReportRepository salesReportRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    public DataBaseService(ContractRepository contractRepository, MedicineRepository medicineRepository, WorkPlaceRepository workPlaceRepository, UserRepository userRepository, UserService userService, DistrictRegionService districtRegionService, DistrictRepository districtRepository, MNNRepository mnnRepository) {
        this.contractRepository = contractRepository;
        this.medicineRepository = medicineRepository;
        this.workPlaceRepository = workPlaceRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.districtRegionService = districtRegionService;
        this.districtRepository = districtRepository;
        this.mnnRepository = mnnRepository;
    }

    public Medicine saveOrUpdateMedicine(Medicine medicine) {
        if (medicine.getId() == null || medicine.getId() == 0) {
            medicine.setCreatedDate(LocalDateTime.now());
        } else {
            // Ensure createdDate is preserved for updates
            Medicine existing = medicineRepository.findById(medicine.getId()).orElse(null);
            if (existing != null) {
                medicine.setCreatedDate(existing.getCreatedDate());
            }
        }
        return medicineRepository.save(medicine);
    }

    public void saveList(List<MedicineDTO> medicines) {
        for (MedicineDTO dto : medicines) {
            Medicine medicine = convertToMedicineEntity(dto);
            saveOrUpdateMedicine(medicine);
        }
    }

    public Medicine convertToMedicineEntity(MedicineDTO dto) {
        Medicine medicine;
        // Only fetch existing entity if ID is provided and not 0
        if (dto.getId() != null && dto.getId() != 0) {
            medicine = medicineRepository.findById(dto.getId()).orElse(new Medicine());
        } else {
            medicine = new Medicine();
        }

        // Do not set ID explicitly; let JPA handle it for new entities
        medicine.setName(dto.getName());
        medicine.setNameUzCyrillic(dto.getNameUzCyrillic());
        medicine.setNameUzLatin(dto.getNameUzLatin());
        medicine.setNameRussian(dto.getNameRussian());
        medicine.setStatus(dto.getStatus());
        // Do not set createdDate here; handle in saveOrUpdateMedicine
        medicine.setImageUrl(dto.getImageUrl());
        medicine.setMnn(mnnRepository.findAllById(dto.getMnn()));
        medicine.setCip(dto.getCip());
        medicine.setQuantity(dto.getQuantity());
        medicine.setNoMore(dto.getNoMore());
        medicine.setPrescription(dto.getPrescription());
        medicine.setVolume(dto.getVolume());
        medicine.setType(dto.getType());
        medicine.setRecipePercentage(dto.getRecipePercentage());
        medicine.setRecipeLimit(dto.getRecipeLimit());
        medicine.setRecipeBall(dto.getRecipeBall());
        medicine.setSuPercentage(dto.getSuPercentage());
        medicine.setSuLimit(dto.getSuLimit());
        medicine.setSuBall(dto.getSuBall());
        medicine.setSbPercentage(dto.getSbPercentage());
        medicine.setSbLimit(dto.getSbLimit());
        medicine.setSbBall(dto.getSbBall());
        medicine.setGzPercentage(dto.getGzPercentage());
        medicine.setGzLimit(dto.getGzLimit());
        medicine.setGzBall(dto.getGzBall());
        medicine.setKbPercentage(dto.getKbPercentage());
        medicine.setKbLimit(dto.getKbLimit());
        medicine.setKbBall(dto.getKbBall());

        return medicine;
    }
    // Delete a Medicine by ID
    @Transactional
    public void deleteMedicine(Long medicineId) {
        try {
            Optional<Medicine> medicineOpt = medicineRepository.findById(medicineId);
            if (medicineOpt.isEmpty()) {
                throw new DataBaseException("Medicine with ID " + medicineId + " not found");
            }

            // Remove MNN references from medicine_mnn join table
            medicineRepository.deleteMedicineMnnReferences(medicineId);

            // Remove references from related entities
            // 1. MedicineAgentGoalQuantity
            List<MedicineAgentGoalQuantity> agentGoalQuantities = medicineRepository.findAgentGoalQuantitiesByMedicineId(medicineId);
            medicineAgentGoalQuantityRepository.deleteAll(agentGoalQuantities);

            // 2. MedicineManagerGoalQuantity
            List<MedicineManagerGoalQuantity> managerGoalQuantities = medicineRepository.findManagerGoalQuantitiesByMedicineId(medicineId);
            medicineManagerGoalQuantityRepository.deleteAll(managerGoalQuantities);

            // 3. MedicineWithQuantityDoctor
            List<MedicineWithQuantityDoctor> withQuantityDoctors = medicineRepository.findWithQuantityDoctorByMedicineId(medicineId);
            medicineWithQuantityDoctorRepository.deleteAll(withQuantityDoctors);

            // 4. OutOfContractMedicineAmount
            List<OutOfContractMedicineAmount> outOfContractAmounts = medicineRepository.findOutOfContractByMedicineId(medicineId);
            outOfContractMedicineAmountRepository.deleteAll(outOfContractAmounts);

            // 5. Sales
            List<Sales> sales = medicineRepository.findSalesByMedicineId(medicineId);
            salesRepository.deleteAll(sales);

            // 6. SalesReport
            List<SalesReport> salesReports = medicineRepository.findSalesReportsByMedicineId(medicineId);
            salesReportRepository.deleteAll(salesReports);

            // 7. Recipe (remove Preparations referencing the Medicine)
            List<Recipe> recipes = medicineRepository.findRecipesByMedicineId(medicineId);
            for (Recipe recipe : recipes) {
                recipe.getPreparations().removeIf(preparation -> preparation.getMedicine().getId().equals(medicineId));
                recipeRepository.save(recipe);
            }

            // 8. Template (remove Preparations referencing the Medicine)
            List<Template> templates = medicineRepository.findTemplatesByMedicineId(medicineId);
            for (Template template : templates) {
                template.getPreparations().removeIf(preparation -> preparation.getMedicine().getId().equals(medicineId));
                templateRepository.save(template);
            }

            // Delete the Medicine
            medicineRepository.deleteById(medicineId);
        } catch (Exception e) {
            throw new DataBaseException("Failed to delete Medicine with ID " + medicineId + ": " + e.getMessage());
        }
    }

    // Find a Medicine by ID
    public Optional<Medicine> findMedicineById(Long id) {
        return medicineRepository.findById(id);
    }

    public List<Medicine> findAllMedicines() {
        return medicineRepository.findAllSortByCreatedDate();
    }


    public Contract getContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public void deleteContract(Long contractId) {
        contractRepository.deleteById(contractId);
    }


    public void createWorkPlace(WorkPlaceDTO workPlaceDTO) {
        WorkPlace workPlace = convertToEntity(workPlaceDTO);
        workPlaceRepository.save(workPlace);
    }

    private WorkPlace convertToEntity(WorkPlaceDTO workPlaceDTO) {
        WorkPlace workPlace = new WorkPlace();
        workPlace.setName(workPlaceDTO.getName());
        workPlace.setEmail(workPlaceDTO.getEmail());
        workPlace.setPhone(workPlaceDTO.getPhone());
        workPlace.setAddress(workPlaceDTO.getAddress());
        workPlace.setDescription(workPlaceDTO.getDescription());
        workPlace.setMedicalInstitutionType(workPlaceDTO.getMedicalInstitutionType());
        workPlace.setChiefDoctor(workPlaceDTO.getChiefDoctorId() != null ? userRepository.findById(workPlaceDTO.getChiefDoctorId()).orElseThrow(() -> new DataBaseException("ChiefDoctor not found")) : null);
        workPlace.setDistrict(districtRepository.findById(workPlaceDTO.getDistrictId()).orElseThrow(() -> new DataBaseException("District not found")));
        return workPlace;
    }


    public void updateWorkPlace(Long id, WorkPlaceDTO workPlaceDTO) {
        WorkPlace existingWorkPlace = workPlaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkPlace not found with id: " + id));

        existingWorkPlace.setName(workPlaceDTO.getName());
        existingWorkPlace.setAddress(workPlaceDTO.getAddress());
        existingWorkPlace.setDescription(workPlaceDTO.getDescription());
        existingWorkPlace.setMedicalInstitutionType(workPlaceDTO.getMedicalInstitutionType());
        existingWorkPlace.setEmail(workPlaceDTO.getEmail());
        existingWorkPlace.setPhone(workPlaceDTO.getPhone());
        existingWorkPlace.setChiefDoctor(workPlaceDTO.getChiefDoctorId() != null ? userRepository.findById(workPlaceDTO.getChiefDoctorId()).orElseThrow(() -> new DataBaseException("ChiefDoctor not found")) : null);
        existingWorkPlace.setDistrict(districtRepository.findById(workPlaceDTO.getDistrictId()).orElseThrow(() -> new DataBaseException("District not found")));


        workPlaceRepository.save(existingWorkPlace);
    }

    public void deleteWorkPlace(Long id) {
        WorkPlace workPlace = workPlaceRepository.findById(id).orElseThrow(() -> new DataBaseException("WorkPlace doesn't exist with id: " + id));
        workPlace.setStatus(Status.DELETED);
        workPlace.setDistrict(null);
        workPlaceRepository.save(workPlace);
    }


    public List<WorkPlaceListDTO> findWorkPlacesByFilters(Long districtId, Long regionId, MedicalInstitutionType medicalInstitutionType) {
        List<WorkPlace> workplaces = workPlaceRepository.findByFilters(districtId, regionId, medicalInstitutionType);
        return workplaces.stream()
                .map(workPlace -> new WorkPlaceListDTO(
                        workPlace.getId(),
                        workPlace.getChiefDoctor() == null ? null : userService.convertToDTO(workPlace.getChiefDoctor()),
                        districtRegionService.regionDistrictDTO(workPlace.getDistrict()),
                        workPlace.getMedicalInstitutionType(),
                        workPlace.getAddress(),
                        workPlace.getDescription(),
                        workPlace.getPhone(),
                        workPlace.getEmail(),
                        workPlace.getName(),
                        workPlace.getChiefDoctor() == null ? null : workPlace.getChiefDoctor().getUserId()))
                .collect(Collectors.toList());

    }

    public WorkPlaceListDTO getWorkPlaceById(Long workplaceId) {
        WorkPlace workPlace = workPlaceRepository.findById(workplaceId).orElse(null);
        return new WorkPlaceListDTO(
                workPlace.getId(),
                workPlace.getChiefDoctor() == null ? null : userService.convertToDTO(workPlace.getChiefDoctor()),
                workPlace.getDistrict() != null ? districtRegionService.regionDistrictDTO(workPlace.getDistrict()) : null,
                workPlace.getMedicalInstitutionType(),
                workPlace.getAddress(),
                workPlace.getDescription(),
                workPlace.getPhone(),
                workPlace.getEmail(),
                workPlace.getName(),
                workPlace.getChiefDoctor() == null ? null : workPlace.getChiefDoctor().getUserId()
        );
    }

    public WorkPlaceStatisticsInfoDTO getWorkPlaceStats(Long workplaceId) {
        WorkPlaceStatisticsInfoDTO workPlaceStatisticsInfoDTO = new WorkPlaceStatisticsInfoDTO();
        List<User> userList = userRepository.findDoctorsByWorkPlaceId(workplaceId, Role.DOCTOR);

        workPlaceStatisticsInfoDTO.setAllDoctors(userList.size());

        Map<Field, FieldStatistics> fieldStatisticsMap = new HashMap();


        for (Field field : Field.values()) {
            fieldStatisticsMap.put(field, new FieldStatistics(field, 0, 0, 0));
        }

        for (User user : userList) {
            Field field = user.getFieldName();
            fieldStatisticsMap.get(field).incrementAllDoctors();
        }
        workPlaceStatisticsInfoDTO.setFieldList(new ArrayList<>(fieldStatisticsMap.values()));


        return workPlaceStatisticsInfoDTO;
    }

    public void bulkWorkPlace(List<WorkPlaceDTO> workPlaceDTOList) {
        for (WorkPlaceDTO workPlaceDTO : workPlaceDTOList) {
            createWorkPlace(workPlaceDTO);
        }
    }

    public MNN saveMNN(MNN mnn) {
        if (mnn != null && mnn.getId() != null && mnn.getId() != 0) {
            Optional<MNN> optional = mnnRepository.findById(mnn.getId());
            if (optional == null || optional.isEmpty()) {
                return mnnRepository.save(mnn);
            }
            MNN mnn1 = optional.get();
            mnn1.setName(mnn.getName());
            mnn1.setType(mnn.getType());
            mnn1.setDosage(mnn.getDosage());
            mnn1.setCombination(mnn.getCombination());
            mnn1.setLatinName(mnn.getLatinName());
            mnn1.setPharmacotherapeuticGroup(mnn.getPharmacotherapeuticGroup());
            mnn1.setWm_ru(mnn.getWm_ru());
            mnnRepository.save(mnn1);


        }
        return mnnRepository.save(mnn);
    }


    public List<MNN> getAllMnn() {
        return mnnRepository.findAllByOrderByNameAsc();
    }

    public List<MNN> getAllByOrderedId() {
        return mnnRepository.findAllByOrderById();
    }

    public Map<Long, MNN> saveMNNList(List<MNN> mnns) {
        Map<Long, MNN> errors = new HashMap<>();

        System.out.println("in process------------------------------");
        for (MNN mnn : mnns) {
            try {
                saveMNN(mnn);
            } catch (Exception e) {
                errors.put(mnn.getId(), mnn);
            }
        }
        System.out.println("Done Saving -------------------------------------");

        return errors;
    }


    public Page<MNN> getAllMnnPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return mnnRepository.findAll(pageable);
    }

    public Page<MNN> getAllMnnPaginatedByOrderedId(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return mnnRepository.findAll(pageable);
    }

    public Page<Medicine> findAllMedicinesPageable(Pageable pageable) {
        return medicineRepository.findAllSortByCreatedDatePageable(pageable);

    }

    public List<Long> deleteMNNs(List<Long> mnnIds) {
        List<Long> deletedIds = new ArrayList<>();
        List<BulkSaveException.ErrorDetail> errors = new ArrayList<>();

        System.out.println("Deleting MNNs in process------------------------------");
        for (Long id : mnnIds) {
            try {
                Optional<MNN> mnn = mnnRepository.findById(id);
                if (mnn.isPresent()) {
                    // Remove MNN from all Medicines
                    List<Medicine> medicines = medicineRepository.findByMnnId(id);
                    for (Medicine medicine : medicines) {
                        medicine.getMnn().removeIf(m -> m.getId().equals(id));
                        medicineRepository.save(medicine);
                    }
                    // Delete the MNN
                    mnnRepository.deleteById(id);
                    deletedIds.add(id);
                } else {
                    errors.add(new BulkSaveException.ErrorDetail(null, "MNN with ID " + id + " not found"));
                }
            } catch (Exception e) {
                errors.add(new BulkSaveException.ErrorDetail(null, e.getMessage()));
            }
        }
        System.out.println("Done Deleting -------------------------------------");

        if (!errors.isEmpty()) {
            throw new BulkSaveException(errors);
        }
        return deletedIds;
    }

    public List<Long> deleteAllMNNs() {
        List<MNN> mnnList = mnnRepository.findAll();
        List<Long> notDeletedIds = new ArrayList<>();
        for (MNN mnn : mnnList) {
            try {
                // Remove MNN from all Medicines
                List<Medicine> medicines = medicineRepository.findByMnnId(mnn.getId());
                for (Medicine medicine : medicines) {
                    medicine.getMnn().removeIf(m -> m.getId().equals(mnn.getId()));
                    medicineRepository.save(medicine);
                }
                // Delete the MNN
                mnnRepository.delete(mnn);
            } catch (Exception e) {
                notDeletedIds.add(mnn.getId());
            }
        }
        return notDeletedIds;
    }

    public void deleteMNN(Long mnnId) {
        try {
            Optional<MNN> mnnOpt = mnnRepository.findById(mnnId);
            if (mnnOpt.isEmpty()) {
                throw new RuntimeException("MNN with ID " + mnnId + " not found");
            }

            // Remove MNN from all Medicines
            List<Medicine> medicines = medicineRepository.findByMnnId(mnnId);
            for (Medicine medicine : medicines) {
                medicine.getMnn().removeIf(mnn -> mnn.getId().equals(mnnId));
                medicineRepository.save(medicine);
            }

            // Delete the MNN
            mnnRepository.deleteById(mnnId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete MNN with ID " + mnnId + ": " + e.getMessage());
        }
    }

    public List<MNN> parseFileMNN(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(
                file.getInputStream(),
                new TypeReference<List<MNN>>() {
                }
        );
    }

    public List<WorkPlaceListDTO> getWorkPlacesByIds(List<Long> regionIds, Long regionId, Long districtId, MedicalInstitutionType medicalInstitutionType) {
        List<WorkPlace> workplaces = workPlaceRepository.findByFilters(regionIds, regionId, districtId, medicalInstitutionType);
        return workplaces.stream()
                .map(workPlace -> new WorkPlaceListDTO(
                        workPlace.getId(),
                        workPlace.getChiefDoctor() == null ? null : userService.convertToDTO(workPlace.getChiefDoctor()),
                        districtRegionService.regionDistrictDTO(workPlace.getDistrict()),
                        workPlace.getMedicalInstitutionType(),
                        workPlace.getAddress(),
                        workPlace.getDescription(),
                        workPlace.getPhone(),
                        workPlace.getEmail(),
                        workPlace.getName(),
                        workPlace.getChiefDoctor() == null ? null : workPlace.getChiefDoctor().getUserId()
                ))
                .collect(Collectors.toList());
    }

    public Page<MNN> getAllMnnPaginatedSearch(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return mnnRepository.findMnnByNameAndSearch(query,pageable);
    }
}