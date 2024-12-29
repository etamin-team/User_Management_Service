package com.example.user_management_service.service;

import com.example.user_management_service.model.Medicine;
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

    private final MedicineRepository medicineRepository;

    @Autowired
    public DataBaseService(MedicineRepository medicineRepository) {
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

    // List all Medicines
    public List<Medicine> findAllMedicines() {
        return medicineRepository.findAll();
    }
}