package com.example.user_management_service.controller;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.ContractDTO;
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

    @PostMapping("/medicine")
    public ResponseEntity<Medicine> createOrUpdateMedicine(@RequestBody Medicine medicine) {
        Medicine savedMedicine = dataBaseService.saveOrUpdateMedicine(medicine);
        return new ResponseEntity<>(savedMedicine, HttpStatus.CREATED);
    }

    @DeleteMapping("/medicine/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        dataBaseService.deleteMedicine(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/medicine/{id}")
    public ResponseEntity<Medicine> getMedicine(@PathVariable Long id) {
        Optional<Medicine> medicine = dataBaseService.findMedicineById(id);
        if (medicine.isPresent()) {
            return new ResponseEntity<>(medicine.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/medicines")
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        List<Medicine> medicines = dataBaseService.findAllMedicines();
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }


    @GetMapping("/contracts/{contractId}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long contractId) {
        Contract contract = dataBaseService.getContractById(contractId);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/contracts")
    public ResponseEntity<List<Contract>> getAllContracts() {
        List<Contract> contracts = dataBaseService.getAllContracts();
        return ResponseEntity.ok(contracts);
    }

    @DeleteMapping("/contracts/{contractId}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long contractId) {
        dataBaseService.deleteContract(contractId);
        return ResponseEntity.noContent().build();
    }
}
