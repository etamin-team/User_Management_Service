package com.example.user_management_service.service;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.ContractRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.WorkPlaceRepository;
import com.example.user_management_service.role.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public DataBaseService(ContractRepository contractRepository, MedicineRepository medicineRepository, WorkPlaceRepository workPlaceRepository, UserRepository userRepository, UserService userService, DistrictRegionService districtRegionService) {
        this.contractRepository = contractRepository;
        this.medicineRepository = medicineRepository;
        this.workPlaceRepository = workPlaceRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.districtRegionService = districtRegionService;
    }

    // Create or update a Medicine (save)
    public Medicine saveOrUpdateMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    // Delete a Medicine by ID
    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }

    // Find a Medicine by ID
    public Optional<Medicine> findMedicineById(Long id) {
        return medicineRepository.findById(id);
    }

    public List<Medicine> findAllMedicines() {
        return medicineRepository.findAll();
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
        workPlace.setAddress(workPlaceDTO.getAddress());
        workPlace.setDescription(workPlaceDTO.getDescription());
        workPlace.setMedicalInstitutionType(workPlaceDTO.getMedicalInstitutionType());
        workPlace.setChiefDoctor(workPlaceDTO.getChiefDoctorId() != null ? userRepository.findById(workPlaceDTO.getChiefDoctorId()).orElseThrow(() -> new RuntimeException("ChiefDoctor not found")) : null);
        District district = new District();
        district.setId(workPlaceDTO.getDistrictId());
        workPlace.setDistrict(district);
        return workPlace;
    }


    public void updateWorkPlace(Long id, WorkPlaceDTO workPlaceDTO) {
        WorkPlace existingWorkPlace = workPlaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkPlace not found with id: " + id));

        existingWorkPlace.setName(workPlaceDTO.getName());
        existingWorkPlace.setAddress(workPlaceDTO.getAddress());
        existingWorkPlace.setDescription(workPlaceDTO.getDescription());

        if (workPlaceDTO.getDistrictId() != null) {
            District District = new District();
            District.setId(workPlaceDTO.getDistrictId());
            existingWorkPlace.setDistrict(District);
        }

        workPlaceRepository.save(existingWorkPlace);
    }

    public void deleteWorkPlace(Long id) {
        workPlaceRepository.deleteById(id);
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
                        workPlace.getEmail()))
                .collect(Collectors.toList());

    }

    public WorkPlaceListDTO getWorkPlaceById(Long workplaceId) {
        WorkPlace workPlace = workPlaceRepository.findById(workplaceId).orElse(null);
        return new WorkPlaceListDTO(
                workPlace.getId(),
                workPlace.getChiefDoctor() == null ? null : userService.convertToDTO(workPlace.getChiefDoctor()),
                districtRegionService.regionDistrictDTO(workPlace.getDistrict()),
                workPlace.getMedicalInstitutionType(),
                workPlace.getAddress(),
                workPlace.getDescription(),
                workPlace.getPhone(),
                workPlace.getEmail());
    }

    public WorkPlaceStatisticsInfoDTO getWorkPlaceStats(Long workplaceId) {
        WorkPlaceStatisticsInfoDTO workPlaceStatisticsInfoDTO = new WorkPlaceStatisticsInfoDTO();
        List<User> userList = userRepository.findDoctorsByWorkPlaceId(workplaceId, Role.DOCTOR);

        workPlaceStatisticsInfoDTO.setAllDoctors(userList.size());

        Map<Field,FieldStatistics> fieldStatisticsMap=new HashMap();



        for (Field field : Field.values()) {
            fieldStatisticsMap.put(field, new FieldStatistics(0, 0, 0));
        }

        for (User user : userList) {
            Field field = user.getFieldName();
            fieldStatisticsMap.get(field).incrementAllDoctors();
        }
        workPlaceStatisticsInfoDTO.setFieldList(new ArrayList<>(fieldStatisticsMap.values()));


        return workPlaceStatisticsInfoDTO;
    }
}