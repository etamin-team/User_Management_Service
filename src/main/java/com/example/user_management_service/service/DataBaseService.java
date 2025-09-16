package com.example.user_management_service.service;

import com.example.user_management_service.exception.BulkSaveException;
import com.example.user_management_service.exception.DataBaseException;
import com.example.user_management_service.exception.NotFoundException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.model.v2.DoctorContractV2; // Import V2 Contract
import com.example.user_management_service.model.v2.MedicineWithQuantityDoctorV2; // Import V2 MedicineWithQuantityDoctor
import com.example.user_management_service.model.v2.OutOfContractMedicineAmountV2; // Import V2 OutOfContractMedicineAmount
import com.example.user_management_service.repository.*;
import com.example.user_management_service.repository.v2.DoctorContractV2Repository;
import com.example.user_management_service.repository.v2.MedicineWithQuantityDoctorV2Repository; // Import V2 MedicineWithQuantityDoctor Repository
import com.example.user_management_service.repository.v2.OutOfContractMedicineAmountV2Repository; // Import V2 OutOfContractMedicineAmount Repository
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
    // Removed: private final ContractRepository contractRepository; // V1 - Commented out by user
    private final MedicineRepository medicineRepository;
    private final WorkPlaceRepository workPlaceRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DistrictRegionService districtRegionService;
    private final DistrictRepository districtRepository;
    private final MNNRepository mnnRepository;

    // --- V2 Repositories ---
    private final DoctorContractV2Repository doctorContractV2Repository; // V2 Contract Repository
    private final MedicineWithQuantityDoctorV2Repository medicineWithQuantityDoctorV2Repository; // V2 MedicineWithQuantityDoctor Repository
    private final OutOfContractMedicineAmountV2Repository outOfContractMedicineAmountV2Repository; // V2 OutOfContractMedicineAmount Repository


    // --- Remaining V1/Common Repositories (assuming they are still in use) ---
    private final SalesRepository salesRepository; // Assuming V1/Common
    private final SalesReportRepository salesReportRepository; // Assuming V1/Common
    private final RecipeRepository recipeRepository; // Assuming V1/Common
    private final TemplateRepository templateRepository; // Assuming V1/Common


    @Autowired // Combined constructor for all final fields
    public DataBaseService(
            MedicineRepository medicineRepository,
            WorkPlaceRepository workPlaceRepository,
            UserRepository userRepository,
            UserService userService,
            DistrictRegionService districtRegionService,
            DistrictRepository districtRepository,
            MNNRepository mnnRepository,
            // V2 Repositories
            DoctorContractV2Repository doctorContractV2Repository,
            MedicineWithQuantityDoctorV2Repository medicineWithQuantityDoctorV2Repository,
            OutOfContractMedicineAmountV2Repository outOfContractMedicineAmountV2Repository,
            // Remaining/Common Repositories
            SalesRepository salesRepository,
            SalesReportRepository salesReportRepository,
            RecipeRepository recipeRepository,
            TemplateRepository templateRepository
    ) {
        this.medicineRepository = medicineRepository;
        this.workPlaceRepository = workPlaceRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.districtRegionService = districtRegionService;
        this.districtRepository = districtRepository;
        this.mnnRepository = mnnRepository;
        // V2 Repositories
        this.doctorContractV2Repository = doctorContractV2Repository;
        this.medicineWithQuantityDoctorV2Repository = medicineWithQuantityDoctorV2Repository;
        this.outOfContractMedicineAmountV2Repository = outOfContractMedicineAmountV2Repository;
        // Remaining/Common Repositories
        this.salesRepository = salesRepository;
        this.salesReportRepository = salesReportRepository;
        this.recipeRepository = recipeRepository;
        this.templateRepository = templateRepository;
    }


    public Medicine saveOrUpdateMedicine(Medicine medicine) {
        if (medicine.getId() == null || medicine.getId() == 0) {
            // Auto-generate ID by finding max ID and incrementing
            Long maxId = medicineRepository.findMaxId().orElse(0L);
            medicine.setId(maxId + 1);
            medicine.setCreatedDate(LocalDateTime.now());
        } else {
            // Preserve createdDate for existing entities
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
        if (dto.getId() != null && dto.getId() != 0) {
            // Use provided ID
            medicine = medicineRepository.findById(dto.getId()).orElse(new Medicine());
            medicine.setId(dto.getId()); // Respect manual ID
        } else {
            // New entity, ID will be set in saveOrUpdateMedicine
            medicine = new Medicine();
        }

        medicine.setName(dto.getName());
        medicine.setNameUzCyrillic(dto.getNameUzCyrillic());
        medicine.setNameUzLatin(dto.getNameUzLatin());
        medicine.setNameRussian(dto.getNameRussian());
        medicine.setStatus(dto.getStatus());
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

    public List<Long> deleteAllMedicines() {
        List<Medicine> medicinesList = medicineRepository.findAll();
        List<Long> notDeletedIds = new ArrayList<>();
        for (Medicine medicine : medicinesList) {
            try {
                deleteMedicine(medicine.getId());
            } catch (Exception e) {
                notDeletedIds.add(medicine.getId());
            }
        }
        return notDeletedIds;
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

            // Remove references from related entities (V2 versions if applicable)
            // Note: MedicineAgentGoalQuantity and MedicineManagerGoalQuantity are V1 entities
            // If they have no V2 direct replacement, these lines would typically be removed.
            // Assuming they are fully deprecated for V2.

            // 1. MedicineWithQuantityDoctorV2
            List<MedicineWithQuantityDoctorV2> withQuantityDoctorsV2 = medicineRepository.findWithQuantityDoctorV2ByMedicineId(medicineId);
            medicineWithQuantityDoctorV2Repository.deleteAll(withQuantityDoctorsV2);

            // 2. OutOfContractMedicineAmountV2
            List<OutOfContractMedicineAmountV2> outOfContractAmountsV2 = medicineRepository.findOutOfContractV2ByMedicineId(medicineId);
            outOfContractMedicineAmountV2Repository.deleteAll(outOfContractAmountsV2);

            // 3. Sales (Assuming Sales is still a valid entity)
            List<Sales> sales = medicineRepository.findSalesByMedicineId(medicineId);
            salesRepository.deleteAll(sales);

            // 4. SalesReport (Assuming SalesReport is still a valid entity)
            List<SalesReport> salesReports = medicineRepository.findSalesReportsByMedicineId(medicineId);
            salesReportRepository.deleteAll(salesReports);

            // 5. Recipe (remove Preparations referencing the Medicine - Assuming Recipe is still a valid entity)
            List<Recipe> recipes = medicineRepository.findRecipesByMedicineId(medicineId);
            for (Recipe recipe : recipes) {
                // Ensure the comparison with getMedicine() is robust, assuming 'Preparation' has a 'Medicine' field
                recipe.getPreparations().removeIf(preparation -> preparation.getMedicine() != null && preparation.getMedicine().getId().equals(medicineId));
                recipeRepository.save(recipe); // Re-save recipe after modification
            }

            // 6. Template (remove Preparations referencing the Medicine - Assuming Template is still a valid entity)
            List<Template> templates = medicineRepository.findTemplatesByMedicineId(medicineId);
            for (Template template : templates) {
                // Ensure the comparison with getMedicine() is robust
                template.getPreparations().removeIf(preparation -> preparation.getMedicine() != null && preparation.getMedicine().getId().equals(medicineId));
                templateRepository.save(template); // Re-save template after modification
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

    // Removed V1 Contract methods, as they are now handled by DoctorServiceV2 or ContractService (V2)
    // public DoctorContractV2 getContractById(Long contractId) { ... }
    // public List<DoctorContractV2> getAllContracts() { ... }
    // public void deleteContract(Long contractId) { ... }


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
        workPlace.setDistrict(null); // Clear district association if needed before saving
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
        if (workPlace == null) {
            throw new NotFoundException("WorkPlace not found with ID: " + workplaceId); // Or handle as per service logic
        }
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

        Map<Field, FieldStatistics> fieldStatisticsMap = new HashMap<>(); // Use diamond operator for cleaner code


        for (Field field : Field.values()) {
            fieldStatisticsMap.put(field, new FieldStatistics(field, 0, 0, 0));
        }

        for (User user : userList) {
            Field field = user.getFieldName();
            if(field != null) { // Add null check for field
                fieldStatisticsMap.get(field).incrementAllDoctors();
            }
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
            if (optional.isEmpty()) { // Check if empty, then save as new
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
            return mnnRepository.save(mnn1); // Save updated existing MNN
        }
        return mnnRepository.save(mnn); // Save new MNN
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
        return medicineRepository.findAllSortByCreatedDatePageable(pageable); // Fixed typo here
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
                    medicine.getMnn().removeIf(m -> m != null && m.getId().equals(mnn.getId())); // Added null check for m
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
                medicine.getMnn().removeIf(mnnEntry -> mnnEntry != null && mnnEntry.getId().equals(mnnId)); // Renamed param to avoid confusion, added null check
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