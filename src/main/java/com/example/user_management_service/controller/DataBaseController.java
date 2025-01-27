package com.example.user_management_service.controller;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.ContractDTO;
import com.example.user_management_service.model.dto.SalesDTO;
import com.example.user_management_service.model.dto.WorkPlaceDTO;
import com.example.user_management_service.service.AdminService;
import com.example.user_management_service.service.DataBaseService;
import com.example.user_management_service.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
    private final SalesService salesService;

    @Autowired
    public DataBaseController(DataBaseService dataBaseService, SalesService salesService) {
        this.dataBaseService = dataBaseService;
        this.salesService = salesService;
    }



    // medicine
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





    // contracts
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



    /// Sales
    @PostMapping("/sales/load-data")
    public ResponseEntity<String> loadData(@RequestBody List<SalesDTO> salesDTOS) {
        try {
            salesService.saveSalesDTOList(salesDTOS);
            return ResponseEntity.ok("Sales data saved successfully.");
        } catch (ResponseStatusException e) {
            // Return the error message with the status code
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
    @PutMapping("/sales/{salesId}")
    public ResponseEntity<String> updateSales(@PathVariable Long salesId, @RequestBody SalesDTO salesDTO) {
        try {
            salesService.updateSales(salesId, salesDTO);
            return ResponseEntity.ok("Sales data updated successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @DeleteMapping("/sales/{salesId}")
    public ResponseEntity<String> deleteSales(@PathVariable Long salesId) {
        try {
            salesService.deleteSales(salesId);
            return ResponseEntity.ok("Sales data deleted successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/sales/data")
    public ResponseEntity<Page<SalesDTO>> getSalesInfoByMedicine(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SalesDTO> salesInfo = salesService.getSalesData(startDate, endDate, page, size);
        return ResponseEntity.ok(salesInfo);
    }


    // workplace

    @PostMapping("/workplaces/create")
    public ResponseEntity<String> createWorkPlace(@RequestBody WorkPlaceDTO workPlaceDTO) {
        dataBaseService.createWorkPlace(workPlaceDTO);
        return new ResponseEntity<>("Workplace created successfully!", HttpStatus.CREATED);
    }

    @PutMapping("/workplaces/{id}")
    public ResponseEntity<String> updateWorkPlace(@PathVariable Long id, @RequestBody WorkPlaceDTO workPlaceDTO) {
        dataBaseService.updateWorkPlace(id, workPlaceDTO);
        return ResponseEntity.ok("Workplace updated successfully!");
    }
    @DeleteMapping("/workplaces/{id}")
    public ResponseEntity<String> deleteWorkPlace(@PathVariable Long id) {
        dataBaseService.deleteWorkPlace(id);
        return ResponseEntity.ok("Workplace deleted successfully!");
    }

}
