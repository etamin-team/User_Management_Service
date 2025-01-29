package com.example.user_management_service.service;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.District;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.model.dto.WorkPlaceDTO;
import com.example.user_management_service.repository.ContractRepository;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.ContractDTO;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.WorkPlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    public DataBaseService(ContractRepository contractRepository, MedicineRepository medicineRepository, WorkPlaceRepository workPlaceRepository) {
        this.contractRepository = contractRepository;
        this.medicineRepository = medicineRepository;
        this.workPlaceRepository = workPlaceRepository;
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


}