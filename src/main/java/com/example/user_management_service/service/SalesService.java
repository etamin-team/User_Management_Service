package com.example.user_management_service.service;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.SalesByRegionDTO;
import com.example.user_management_service.model.dto.SalesDTO;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.SalesByRegionRepository;
import com.example.user_management_service.repository.SalesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final SalesByRegionRepository salesByRegionRepository;
    private final MedicineRepository medicineRepository;
    private final DistrictRepository districtRepository;

    public void saveSalesDTOList(List<SalesDTO> salesDTOS) {
        for (SalesDTO salesDTO : salesDTOS) {
            // Check if Medicine exists
            Medicine medicine = medicineRepository.findById(salesDTO.getMedicineId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Medicine not found for ID: " + salesDTO.getMedicineId()
                    ));

            // Map SalesDTO to Sales entity
            Sales sales = new Sales();
            sales.setSalesDate(salesDTO.getSalesDate());
            sales.setMedicine(medicine);

            // Save Sales to get the ID for SalesByRegion
            Sales savedSales = salesRepository.save(sales);

            // Map and save SalesByRegion entities
            List<SalesByRegion> salesByRegionList = salesDTO.getSalesByRegion().stream()
                    .map(regionDTO -> {
                        District district = districtRepository.findById(regionDTO.getDistrictId())
                                .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "District not found for ID: " + regionDTO.getDistrictId()
                                ));

                        SalesByRegion salesByRegion = new SalesByRegion();
                        salesByRegion.setSales(savedSales);
                        salesByRegion.setDistrict(district);
                        salesByRegion.setDirectSales(regionDTO.getDirectSales());
                        salesByRegion.setSecondarySales(regionDTO.getSecondarySales());
                        salesByRegion.setQuote(regionDTO.getQuote());
                        salesByRegion.setTotalSales(regionDTO.getTotalSales());
                        return salesByRegion;
                    })
                    .collect(Collectors.toList());

            // Save all SalesByRegion entities
            salesByRegionRepository.saveAll(salesByRegionList);
        }
    }

    @Transactional
    public void updateSales(Long salesId, SalesDTO salesDTO) {
        // Find existing Sales record
        Sales existingSales = salesRepository.findById(salesId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sales not found for ID: " + salesId
                ));

        // Update Sales data
        Medicine medicine = medicineRepository.findById(salesDTO.getMedicineId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Medicine not found for ID: " + salesDTO.getMedicineId()
                ));
        existingSales.setSalesDate(salesDTO.getSalesDate());
        existingSales.setMedicine(medicine);
        salesRepository.save(existingSales);

        // Delete existing SalesByRegion for this Sales record
        salesByRegionRepository.deleteBySales(existingSales);

        // Create and save updated SalesByRegion data
        List<SalesByRegion> updatedSalesByRegionList = salesDTO.getSalesByRegion().stream()
                .map(regionDTO -> {
                    District district = districtRepository.findById(regionDTO.getDistrictId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "District not found for ID: " + regionDTO.getDistrictId()
                            ));

                    SalesByRegion salesByRegion = new SalesByRegion();
                    salesByRegion.setSales(existingSales);
                    salesByRegion.setDistrict(district);
                    salesByRegion.setDirectSales(regionDTO.getDirectSales());
                    salesByRegion.setSecondarySales(regionDTO.getSecondarySales());
                    salesByRegion.setQuote(regionDTO.getQuote());
                    salesByRegion.setTotalSales(regionDTO.getTotalSales());
                    return salesByRegion;
                })
                .collect(Collectors.toList());

        salesByRegionRepository.saveAll(updatedSalesByRegionList);
    }
    @Transactional
    public void deleteSales(Long salesId) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sales not found for ID: " + salesId
                ));

        // Delete associated SalesByRegion data
        salesByRegionRepository.deleteBySales(sales);

        // Delete Sales record
        salesRepository.delete(sales);
    }


    public Page<SalesDTO> getSalesData(LocalDate startDate, LocalDate endDate, int page, int size) {
        // Create a Pageable object using page number and size
        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated sales data based on the date range
        Page<Sales> salesPage;
        if (startDate != null && endDate != null) {
            salesPage = salesRepository.findAllBySalesDateBetween(startDate, endDate, pageable);
        } else {
            salesPage = salesRepository.findAll(pageable);
        }

        // Map Sales entities to SalesDTO and return the Page of DTOs
        return salesPage.map(this::mapToSalesDTO);
    }

    private SalesDTO mapToSalesDTO(Sales sales) {
        return new SalesDTO(
                sales.getMedicine().getId(),
                sales.getId(),
                sales.getMedicine().getName(),
                sales.getMedicine().getCip(),
                sales.getSalesDate(),
                sales.getSalesByRegion().stream()
                        .map(region -> new SalesByRegionDTO(
                                region.getDistrict().getId(),
                                region.getDirectSales(),
                                region.getSecondarySales(),
                                region.getQuote(),
                                region.getTotalSales(),
                                region.getSalesDate()
                        ))
                        .collect(Collectors.toList())
        );
    }

}
