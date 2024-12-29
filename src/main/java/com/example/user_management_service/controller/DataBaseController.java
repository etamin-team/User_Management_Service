package com.example.user_management_service.controller;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.service.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Date-12/26/2024
 * By Sardor Tokhirov
 * Time-4:41 AM (GMT+5)
 */

@RestController
@RequestMapping("/api/v1/db")
@CrossOrigin(origins = "*")
public class DataBaseController {

    private final DataBaseService dataBaseService;

    @Autowired
    public DataBaseController(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    // Create a new Medicine or update an existing one
    @PostMapping("/medicine")
    public ResponseEntity<Medicine> createOrUpdateMedicine(@RequestBody Medicine medicine) {
        Medicine savedMedicine = dataBaseService.saveOrUpdateMedicine(medicine);
        return new ResponseEntity<>(savedMedicine, HttpStatus.CREATED);
    }

    // Delete a Medicine by ID
    @DeleteMapping("/medicine/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        dataBaseService.deleteMedicine(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get a Medicine by ID
    @GetMapping("/medicine/{id}")
    public ResponseEntity<Medicine> getMedicine(@PathVariable Long id) {
        Optional<Medicine> medicine = dataBaseService.findMedicineById(id);
        if (medicine.isPresent()) {
            return new ResponseEntity<>(medicine.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all Medicines
    @GetMapping("/medicines")
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        List<Medicine> medicines = dataBaseService.findAllMedicines();
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }
}
