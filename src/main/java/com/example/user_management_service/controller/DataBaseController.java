package com.example.user_management_service.controller;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.MedicalInstitutionType;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Date-12/26/2024
 * By Sardor Tokhirov
 * Time-4:41 AM (GMT+5)
 */

@RestController
@RequestMapping("/api/v1/db")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class DataBaseController {
    private final DistrictRegionService districtRegionService;

    private final DataBaseService dataBaseService;
    private final SalesService salesService;
    private final RecipeService recipeService;
    private final ConditionsToPreparateService conditionsToPreparateService;



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
    public ResponseEntity<String> updateSales(@PathVariable Long salesId, @RequestBody SalesDistrictDTO salesDTO) {
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
    public ResponseEntity<Page<SalesByRegionAndDistrictDTO>> getSalesInfoByMedicine(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SalesByRegionAndDistrictDTO> salesInfo = salesService.getSalesData(startDate, endDate, page, size);
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

    @GetMapping("/workplaces")
    public ResponseEntity<List<WorkPlaceListDTO>> getAllWorkPlaces(
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) MedicalInstitutionType medicalInstitutionType) {

        List<WorkPlaceListDTO> workplaces = dataBaseService.findWorkPlacesByFilters(districtId, regionId, medicalInstitutionType);
        return ResponseEntity.ok(workplaces);
    }

    @GetMapping("/workplaces/{workplaceId}")
    public ResponseEntity<WorkPlaceListDTO> getWorkPlaceById(@PathVariable Long workplaceId) {
        WorkPlaceListDTO workplaces = dataBaseService.getWorkPlaceById(workplaceId);
        return ResponseEntity.ok(workplaces);
    }
    @GetMapping("/workplaces/statistics/{workplaceId}")
    public ResponseEntity<WorkPlaceStatisticsInfoDTO> getWorkPlaceDoctorsById(@PathVariable Long workplaceId) {
        WorkPlaceStatisticsInfoDTO statisticsInfoDTO = dataBaseService.getWorkPlaceStats(workplaceId);
        return ResponseEntity.ok(statisticsInfoDTO);
    }



    //Recipes
    @GetMapping("/recipes")
    public ResponseEntity<Page<RecipeDto>> filterRecipes(
            @RequestParam(required = false) String nameQuery,
            @RequestParam(required = false) UUID regionId,
            @RequestParam(required = false) UUID districtId,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) Field doctorField,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastAnalysisFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastAnalysisTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<RecipeDto> recipes = recipeService.filterRecipes(
                nameQuery, regionId, districtId, medicineId, doctorField,
                lastAnalysisFrom, lastAnalysisTo, page, size
        );
        return ResponseEntity.ok(new PageImpl<>(recipes, PageRequest.of(page, size), recipes.size()));
    }



    //ConditionsToPreparate

    @PostMapping("/conditions")
    public ResponseEntity<ConditionsToPreparateDto> create(@RequestBody ConditionsToPreparateDto dto) {
        return ResponseEntity.ok(conditionsToPreparateService.save(dto));
    }

    @GetMapping("/conditions")
    public ResponseEntity<List<ConditionsToPreparateDto>> getAll() {
        return ResponseEntity.ok(conditionsToPreparateService.getAll());
    }

    @GetMapping("/conditions/{id}")
    public ResponseEntity<ConditionsToPreparateDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(conditionsToPreparateService.getById(id));
    }

    @PutMapping("/conditions/{id}")
    public ResponseEntity<ConditionsToPreparateDto> update(@PathVariable Long id, @RequestBody ConditionsToPreparateDto dto) {
        return ResponseEntity.ok(conditionsToPreparateService.update(id, dto));
    }

    @DeleteMapping("/conditions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        conditionsToPreparateService.delete(id);
        return ResponseEntity.noContent().build();
    }



    //regions and districts


    @PostMapping("/regions/add")
    public ResponseEntity<RegionDTO> addRegion(@RequestBody RegionDTO regionDTO) {
        return ResponseEntity.ok(districtRegionService.addRegion(regionDTO));
    }

    @PostMapping("/regions/add-bulk")
    public ResponseEntity<List<RegionDTO>> addRegions(@RequestBody List<RegionDTO> regionDTOs) {
        return ResponseEntity.ok(districtRegionService.addRegions(regionDTOs));
    }

    @PostMapping("/districts/add")
    public ResponseEntity<DistrictDTO> addDistrict(@RequestBody DistrictDTO districtDTO) {
        return ResponseEntity.ok(districtRegionService.addDistrict(districtDTO));
    }

    @PostMapping("/districts/add-bulk")
    public ResponseEntity<List<DistrictDTO>> addDistricts(@RequestBody List<DistrictDTO> districtDTOs) {
        return ResponseEntity.ok(districtRegionService.addDistricts(districtDTOs));
    }

    @PutMapping("/regions/update/{id}")
    public ResponseEntity<RegionDTO> updateRegion(@PathVariable Long id, @RequestBody RegionDTO regionDTO) {
        return ResponseEntity.ok(districtRegionService.updateRegion(id, regionDTO));
    }

    @DeleteMapping("/regions/delete/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        districtRegionService.deleteRegion(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/districts/update/{id}")
    public ResponseEntity<DistrictDTO> updateDistrict(@PathVariable Long id, @RequestBody DistrictDTO districtDTO) {
        return ResponseEntity.ok(districtRegionService.updateDistrict(id, districtDTO));
    }

    @DeleteMapping("/districts/delete/{id}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        districtRegionService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }
}
