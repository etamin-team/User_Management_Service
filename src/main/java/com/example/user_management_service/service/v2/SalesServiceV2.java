package com.example.user_management_service.service.v2;

import com.example.user_management_service.exception.SalesLoadException;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.Sales;
import com.example.user_management_service.model.dto.SalesByRegionDTO;
import com.example.user_management_service.model.dto.SalesDTO;
import com.example.user_management_service.model.dto.SalesRegionDTO;
import com.example.user_management_service.model.v2.ReportSaving;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RegionRepository;
import com.example.user_management_service.repository.SalesByRegionRepository;
import com.example.user_management_service.repository.SalesRepository;
import com.example.user_management_service.repository.v2.ReportSavingRepository;
import com.example.user_management_service.service.DistrictRegionService;
import com.example.user_management_service.service.ReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date-9/11/2025
 * By Sardor Tokhirov
 * Time-7:25 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SalesServiceV2 {

    private final SalesRepository salesRepository;
    private final MedicineRepository medicineRepository;
    private final RegionRepository regionRepository;
    private final DistrictRegionService districtRegionService;
    private final ReportService reportService;
    private final ReportSavingRepository reportSavingRepository;


    public void saveSalesDTOList(List<SalesDTO> salesDTOS) {
        YearMonth yearMonth = salesDTOS.get(0).getYearMonth();
        // Delete all existing sales for this month
        List<Sales> existingSales = salesRepository.findAllByYearMonth(yearMonth);
        if (!existingSales.isEmpty()) {
            existingSales.forEach(sales -> {
                sales.setRegion(null);
                sales.setMedicine(null);
            });
            salesRepository.saveAll(existingSales);
            salesRepository.deleteAll(existingSales);
        }
        ReportSaving reportSaving = reportSavingRepository.findOneByRegionIdAndYearMonth(existingSales.get(0).getRegion().getId(), yearMonth);
        if (reportSaving == null) {
            reportSaving = new ReportSaving();
            reportSaving.setRegion(existingSales.get(0).getRegion());
            reportSaving.setYearMonth(yearMonth);
            reportSaving.setSaved(false);
            reportSavingRepository.save(reportSaving);
        }        for (SalesDTO salesDTO : salesDTOS) {
            Medicine medicine = medicineRepository.findById(salesDTO.getMedicineId())
                    .orElseThrow(() -> new SalesLoadException("Medicine not found with id:" + salesDTO.getMedicineId()));
    
            for (SalesRegionDTO regionDTO : salesDTO.getSales()) {
                Region region = regionRepository.findById(regionDTO.getRegionId())
                        .orElseThrow(() -> new SalesLoadException("Region not found with id:" + regionDTO.getRegionId()));
    
                Sales sales = new Sales();
                sales.setMedicine(medicine);
                sales.setRegion(region);
                sales.setGroups(salesDTO.getGroup());
                sales.setYearMonth(yearMonth);
                sales.setAllDirectSales(regionDTO.getAllDirectSales());
                sales.setAllSecondarySales(regionDTO.getAllSecondarySales());
                sales.setQuote(regionDTO.getQuote());
                sales.setTotal(regionDTO.getTotal());
                reportService.saveSalesReport(sales);
                salesRepository.save(sales);
            }
        }
    }
    
    /**
     * Updates an existing sales record
     * @param salesId ID of the sales record to update
     * @param salesRegionDTO Updated sales data
     */
    public void updateSales(Long salesId, SalesRegionDTO salesRegionDTO) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new SalesLoadException("Sales record not found"));

        sales.setAllDirectSales(salesRegionDTO.getAllDirectSales());
        sales.setAllSecondarySales(salesRegionDTO.getAllSecondarySales());
        sales.setQuote(salesRegionDTO.getQuote());
        sales.setTotal(salesRegionDTO.getTotal());
        reportService.updateSalesReport(sales);

        salesRepository.save(sales);
    }

    /**
     * Deletes a sales record
     * @param salesId ID of the sales record to delete
     */
    public void deleteSales(Long salesId) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new SalesLoadException("Sales record not found"));
        sales.setRegion(null);
        sales.setMedicine(null);
        Sales save = salesRepository.save(sales);
        salesRepository.delete(save);
    }

    /**
     * Retrieves sales data for a specific year-month with pagination
     * @param yearMonth Year and month for the data
     * @param page Page number
     * @param size Page size
     * @return Paginated sales data
     */
    public Page<SalesByRegionDTO> getSalesData(YearMonth yearMonth, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Sales> salesList = salesRepository.findAllByYearMonth(yearMonth);
    
        List<SalesByRegionDTO> salesDTOList = salesList.stream()
                .map(sale -> new SalesByRegionDTO(
                        sale.getId(),
                        sale.getMedicine().getId(),
                        sale.getGroups(),
                        sale.getAllDirectSales(),
                        sale.getAllSecondarySales(),
                        sale.getQuote(),
                        sale.getTotal(),
                        sale.getMedicine(),
                        districtRegionService.mapRegionToDTO(sale.getRegion())
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(salesDTOList, pageable, salesDTOList.size());
    }
}
