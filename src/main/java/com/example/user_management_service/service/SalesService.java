package com.example.user_management_service.service;

import com.example.user_management_service.exception.SalesLoadException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.SalesByRegionDTO;
import com.example.user_management_service.model.dto.SalesDTO;
import com.example.user_management_service.model.dto.SalesRegionDTO;
import com.example.user_management_service.model.dto.SalesReportDTO;
import com.example.user_management_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class SalesService {

    private final SalesRepository salesRepository;
    private final SalesByRegionRepository salesByRegionRepository;
    private final MedicineRepository medicineRepository;
    private final DistrictRepository districtRepository;
    private final DistrictRegionService districtRegionService;
    private final RegionRepository regionRepository;

    public void saveSalesDTOList(List<SalesDTO> salesDTOS) {
        for (SalesDTO salesDTO : salesDTOS) {
            Medicine medicine = medicineRepository.findById(salesDTO.getMedicineId())
                    .orElseThrow(() -> new SalesLoadException("Medicine not found with id:" + salesDTO.getMedicineId()));

            for (SalesRegionDTO regionDTO : salesDTO.getSales()) {
                Region region = regionRepository.findById(regionDTO.getRegionId())
                        .orElseThrow(() -> new SalesLoadException("Region not found with id:" + regionDTO.getRegionId()));

                Sales sales = new Sales();
                sales.setMedicine(medicine);
                sales.setRegion(region);
                sales.setGroups(salesDTO.getGroup());
                sales.setStartDate(salesDTO.getStartDate());
                sales.setEndDate(salesDTO.getEndDate());
                sales.setAllDirectSales(regionDTO.getAllDirectSales());
                sales.setAllSecondarySales(regionDTO.getAllSecondarySales());
                sales.setQuote(regionDTO.getQuote());
                sales.setTotal(regionDTO.getTotal());

                salesRepository.save(sales);
            }
        }
    }

    public void saveSalesDTO(LocalDate startDate, LocalDate endDate, SalesReportDTO dto, Region region, Medicine medicine) {
            Sales sales = new Sales();
            sales.setMedicine(medicine);
            sales.setRegion(region);
            sales.setStartDate(startDate);
            sales.setEndDate(endDate);
            sales.setAllDirectSales(dto.getWritten());
            sales.setQuote(dto.getAllowed());
            sales.setTotal(dto.getSold());
            salesRepository.save(sales);
    }


    public void updateSales(Long salesId, SalesRegionDTO salesRegionDTO) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new RuntimeException("Sales record not found"));

        sales.setAllDirectSales(salesRegionDTO.getAllDirectSales());
        sales.setAllSecondarySales(salesRegionDTO.getAllSecondarySales());
        sales.setQuote(salesRegionDTO.getQuote());
        sales.setTotal(salesRegionDTO.getTotal());

        salesRepository.save(sales);
    }

    public void deleteSales(Long salesId) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new RuntimeException("Sales record not found"));
        sales.setRegion(null);
        sales.setMedicine(null);
        Sales save = salesRepository.save(sales);
        salesRepository.delete(save);
    }

    public Page<SalesByRegionDTO> getSalesData(LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Sales> salesList = salesRepository.findAllByStartAndEndDate(startDate, endDate);


        List<SalesByRegionDTO> salesDTOList = salesList.stream()
                .map(sale -> new SalesByRegionDTO(
                                sale.getId(),
                                sale.getMedicine().getId(),
                                sale.getGroups(),
                                sale.getStartDate(),
                                sale.getEndDate(),
                                sale.getAllDirectSales(),
                                sale.getAllSecondarySales(),
                                sale.getQuote(),
                                sale.getTotal(),
                                districtRegionService.mapRegionToDTO(sale.getRegion())
                        )
                )
                .collect(Collectors.toList());

        return new PageImpl<>(salesDTOList, pageable, salesDTOList.size());
    }


}
