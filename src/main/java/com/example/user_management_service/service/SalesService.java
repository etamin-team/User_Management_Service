package com.example.user_management_service.service;

import com.example.user_management_service.exception.SalesLoadException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.SalesByRegionAndDistrictDTO;
import com.example.user_management_service.model.dto.SalesDTO;
import com.example.user_management_service.model.dto.SalesDistrictDTO;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.SalesByRegionRepository;
import com.example.user_management_service.repository.SalesRepository;
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

    public void saveSalesDTOList(List<SalesDTO> salesDTOS) {
        for (SalesDTO salesDTO : salesDTOS) {
            Medicine medicine = medicineRepository.findById(salesDTO.getMedicineId())
                    .orElseThrow(() -> new SalesLoadException("Medicine not found with id:" + salesDTO.getMedicineId()));

            for (SalesDistrictDTO districtDTO : salesDTO.getSales()) {
                District district = districtRepository.findById(districtDTO.getDistrictId())
                        .orElseThrow(() -> new SalesLoadException("District not found with id:" + districtDTO.getDistrictId()));

                Sales sales = new Sales();
                sales.setMedicine(medicine);
                sales.setDistrict(district);
                sales.setGroups(salesDTO.getGroup());
                sales.setStartDate(salesDTO.getStartDate());
                sales.setEndDate(salesDTO.getEndDate());
                sales.setAllDirectSales(districtDTO.getAllDirectSales());
                sales.setAllSecondarySales(districtDTO.getAllSecondarySales());
                sales.setQuote(districtDTO.getQuote());
                sales.setTotal(districtDTO.getTotal());

                salesRepository.save(sales);
            }
        }
    }



    public void updateSales(Long salesId, SalesDistrictDTO salesDistrictDTO) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new RuntimeException("Sales record not found"));

        sales.setAllDirectSales(salesDistrictDTO.getAllDirectSales());
        sales.setAllSecondarySales(salesDistrictDTO.getAllSecondarySales());
        sales.setQuote(salesDistrictDTO.getQuote());
        sales.setTotal(salesDistrictDTO.getTotal());

        salesRepository.save(sales);
    }

    public void deleteSales(Long salesId) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new RuntimeException("Sales record not found"));
        salesRepository.delete(sales);
    }

    public Page<SalesByRegionAndDistrictDTO> getSalesData(LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Sales> salesList = salesRepository.findAllByStartAndEndDate(startDate, endDate);


        List<SalesByRegionAndDistrictDTO> salesDTOList = salesList.stream()
                .map(sale -> new SalesByRegionAndDistrictDTO(
                        sale.getId(),
                        sale.getMedicine().getId(),
                        sale.getGroups(),
                        sale.getStartDate(),
                        sale.getEndDate(),
                        sale.getAllDirectSales(),
                        sale.getAllSecondarySales(),
                        sale.getQuote(),
                        sale.getTotal(),
                        districtRegionService.regionDistrictDTO(sale.getDistrict())
                        )
                )
                .collect(Collectors.toList());

        return new PageImpl<>(salesDTOList, pageable, salesDTOList.size());
    }



}
