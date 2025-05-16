package com.example.user_management_service.service;

import com.example.user_management_service.exception.DataBaseException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.role.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Create or update a Medicine (save)
    public Medicine saveOrUpdateMedicine(Medicine medicine) {
        if (medicine.getId() == null || medicine.getId() == 0) {
            medicine.setCreatedDate(LocalDateTime.now());
        }
        return medicineRepository.save(medicine);
    }

    public void saveList(List<Medicine> medicines) {
        for (Medicine medicine : medicines) {
            saveOrUpdateMedicine(medicine);
        }
    }


    // Delete a Medicine by ID
    public void deleteMedicine(Long id) {
        Medicine byId = medicineRepository.findById(id).orElseThrow(() -> new DataBaseException("Medicine not found"));
        byId.setStatus(Status.DELETED);
        medicineRepository.save(byId);
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
                        workPlace.getName()))
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
                workPlace.getName());
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

    public MNN saveMNN(MNN mnn) {
        return mnnRepository.save(mnn);

    }

    public void deleteMNN(Long mnn) {
        mnnRepository.deleteById(mnn);
    }

    public List<MNN> getAllMnn() {
        return mnnRepository.findAll();
    }

    public void saveMNNList(List<MNN> mnns) {
        System.out.println("in process------------------------------");
        mnnRepository.saveAll(mnns);
        System.out.println("Done Saving -------------------------------------");
    }

    public void bulkWorkPlace(List<WorkPlaceDTO> workPlaceDTOList) {
        for (WorkPlaceDTO workPlaceDTO : workPlaceDTOList) {
            createWorkPlace(workPlaceDTO);
        }
    }

    public Page<Medicine> findAllMedicinesPageable(Pageable pageable) {
        return medicineRepository.findAllSortByCreatedDatePageable(pageable);

    }


    public List<MNN> parseFileDoctors(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(
                file.getInputStream(),
                new TypeReference<List<MNN>>() {}
        );
    }
}