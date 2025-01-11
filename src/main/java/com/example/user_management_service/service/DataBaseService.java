package com.example.user_management_service.service;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.repository.ContractRepository;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.ContractDTO;
import com.example.user_management_service.repository.MedicineRepository;
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

    @Autowired
    public DataBaseService(ContractRepository contractRepository, MedicineRepository medicineRepository) {
        this.contractRepository = contractRepository;
        this.medicineRepository = medicineRepository;
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

    public Contract saveContractFromDTO(ContractDTO contractDTO) {
        Contract contract = new Contract();

        contract.setContractType(contractDTO.getContractType());
        contract.setContractStatus(contractDTO.getContractStatus());
        contract.setTotalAmount(contractDTO.getTotalAmount());

        // Set the additional fields
        contract.setQuota_60(contractDTO.getQuota_60());
        contract.setQuota_75_90(contractDTO.getQuota_75_90());
        contract.setSb(contractDTO.getSb());
        contract.setSu(contractDTO.getSu());
        contract.setGz(contractDTO.getGz());
        contract.setKb(contractDTO.getKb());

        // Set medicines
        List<Medicine> medicines = medicineRepository.findAllById(contractDTO.getMedicineIds());
        contract.setMedicines(medicines);

        // Set new fields
        contract.setCreatedAt(contractDTO.getCreatedAt());
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());

        return contractRepository.save(contract);
    }

}