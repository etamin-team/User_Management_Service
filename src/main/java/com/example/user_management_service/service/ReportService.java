package com.example.user_management_service.service;

import com.example.user_management_service.exception.DataNotFoundException;
import com.example.user_management_service.exception.ReportException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.model.v2.DoctorContractV2;
import com.example.user_management_service.model.v2.dto.ContractDTOV2;
import com.example.user_management_service.model.v2.dto.MedicineQuoteDTOV2;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.repository.v2.DoctorContractV2Repository;
import com.example.user_management_service.repository.v2.MedicineWithQuantityDoctorV2Repository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-3:24 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ContractRepository contractRepository;
    private final UserService userService;
    private final ContractService contractService;
    private final SalesReportRepository salesReportRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineWithQuantityDoctorV2Repository medicineWithQuantityDoctorV2Repository;
    private final RegionRepository regionRepository;
    private final SalesService salesService;
    private final SalesRepository salesRepository;
    private final RecipeRepository recipeRepository;
    private final DistrictRegionService districtRegionService;
    private final DoctorContractV2Repository doctorContractV2Repository;

    public SalesReportDTO getSalesReportsByFilters(Long medicineId, ContractType contractType, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, YearMonth yearMonth) {

        SalesReportDTO dto = new SalesReportDTO();

        SalesReport report = salesReportRepository.findByFilters(
                medicineId,
                regionId,
                contractType,
                yearMonth
        ).orElseGet(() -> salesReportRepository.findByFilters(
                medicineId,
                regionId,
                contractType,
                yearMonth.minusMonths(1)
        ).orElseThrow(() -> new ReportException("Report not found")));

        // Update the values with the latest data from v2 repositories
        Long written = medicineWithQuantityDoctorV2Repository.findTotalWritten(medicineId, contractType, regionId, districtId, workplaceId, fieldName, yearMonth);
        Long allowed = medicineWithQuantityDoctorV2Repository.findTotalAllowed(medicineId, contractType, regionId, districtId, workplaceId, fieldName, yearMonth);

        dto.setWritten(written);
        dto.setAllowed(allowed);
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setMedicine(report.getMedicine());
        dto.setId(report.getId());
        dto.setContractType(contractType);
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(medicineId, contractType, query, regionId, districtId, workplaceId, fieldName, yearMonth));

        return dto;
    }

    public SalesReportDTO getSalesReportsByFilters(Long medicineId, ContractType contractType, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, LocalDate startDate, LocalDate endDate) {

        SalesReportDTO dto = new SalesReportDTO();

        // Convert LocalDate to YearMonth for the previous month fallback
        YearMonth currentYearMonth = YearMonth.from(startDate);

        // First try date range query
        List<SalesReport> reports = salesReportRepository.findByDateRange(
                medicineId,
                regionId,
                contractType,
                startDate,
                endDate
        );

        SalesReport report;

        if (reports.isEmpty()) {
            // If no results in date range, try previous month
            report = salesReportRepository.findByFilters(
                    medicineId,
                    regionId,
                    contractType,
                    currentYearMonth.minusMonths(1)
            ).orElseThrow(() -> new ReportException("Report not found"));
        } else {
            report = reports.get(0);
        }
        // Update the values with the latest data from v2 repositories
        Long written = medicineWithQuantityDoctorV2Repository.findTotalWritten(medicineId, contractType, regionId, startDate, endDate);
        Long allowed = medicineWithQuantityDoctorV2Repository.findTotalAllowed(medicineId, contractType, regionId, startDate, endDate);

        dto.setWritten(written);
        dto.setAllowed(allowed);
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setMedicine(report.getMedicine());
        dto.setId(report.getId());
        dto.setContractType(contractType);
        // Since we're filtering by date range, we'll convert to YearMonth for compatibility with doctor reports
        YearMonth yearMonth = reports.isEmpty() ? YearMonth.now() : reports.get(0).getYearMonth();
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(medicineId, contractType, query, regionId, districtId, workplaceId, fieldName, yearMonth));

        return dto;
    }

    public DoctorReportDTO getDoctorReports(Long medicineId, ContractType contractType, String query, Long regionId, Long districtId, Long workplaceId, YearMonth yearMonth, Field fieldName) {
        DoctorReportDTO doctorReportDTO = new DoctorReportDTO();
        Long allowed = medicineWithQuantityDoctorV2Repository.findTotalAllowed(medicineId, contractType, regionId, districtId, workplaceId, fieldName, yearMonth);
        Long written = medicineWithQuantityDoctorV2Repository.findTotalWritten(medicineId, contractType, regionId, districtId, workplaceId, fieldName, yearMonth);
        Long inFact = medicineWithQuantityDoctorV2Repository.findTotalWrittenInFact(medicineId, contractType, regionId, districtId, workplaceId, fieldName, yearMonth);

        doctorReportDTO.setMedicine(medicineRepository.findById(medicineId).orElseThrow(() -> new ReportException("Medicine not found")));
        doctorReportDTO.setAllowed(allowed);
        doctorReportDTO.setWritten(written);
        doctorReportDTO.setWrittenInFact(inFact);
        doctorReportDTO.setDoctorReportListDTOList(getDoctorReportListDTOList(medicineId, contractType, query, regionId, districtId, workplaceId, fieldName, yearMonth));

        return doctorReportDTO;
    }

    public List<DoctorReportListDTO> getDoctorReportListDTOList(Long medicineId, ContractType contractType, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, YearMonth yearMonth) {
        List<DoctorContractV2> contracts = doctorContractV2Repository.findContractsByFilters(medicineId, contractType, query, regionId, districtId, workplaceId, fieldName, yearMonth);

        return contracts.stream()
                .map(contract -> {
                    DoctorReportListDTO dto = new DoctorReportListDTO();
                    dto.setDoctor(userService.convertToDTO(contract.getDoctor()));
                    dto.setContractDTO(convertToContractDTOV2(contract));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DoctorReportListDTO> getDoctorReportListDTOList(List<Long> regionIds, ContractType contractType, Long medicineId, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, YearMonth yearMonth) {
        List<DoctorContractV2> contracts = doctorContractV2Repository.findContractsByFilters(regionIds, contractType, medicineId, query, regionId, districtId, workplaceId, fieldName, yearMonth);

        return contracts.stream()
                .map(contract -> {
                    DoctorReportListDTO dto = new DoctorReportListDTO();
                    dto.setDoctor(userService.convertToDTO(contract.getDoctor()));
                    dto.setContractDTO(convertToContractDTOV2(contract));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void saveSalesReports(SalesReportListDTO salesReportListDTO) {
        for (SalesReportDTO dto : salesReportListDTO.getSalesReportDTOS()) {
            SalesReport report = salesReportRepository.findById(dto.getId()).orElseThrow(() -> new ReportException("SalesReport not found"));
           if(!report.isSaved()){
               Long sold = medicineWithQuantityDoctorV2Repository.findTotalWrittenInFact(report.getMedicine().getId(), report.getContractType(), report.getRegion().getId(), null, null, null, report.getYearMonth());
               report.setReportDate(LocalDate.now());
               report.setWritten(report.getWritten());
               report.setAllowed(report.getAllowed());
               report.setSold(sold);
               report.setSaved(true);
               report.setYearMonth(salesReportListDTO.getYearMonth());
               report.setContractType(dto.getContractType());
               salesReportRepository.save(report);
           }
        }
    }
    public void  openSalesReportEdit(Long regionId, YearMonth yearMonth){
        List<SalesReport> report = salesReportRepository.findByRegionIdAndYearMonth(regionId, yearMonth);
        report.forEach(r -> r.setSaved(false));
        salesReportRepository.saveAll(report);
    }
    public void editSalesReport(Long id, SalesReportDTO salesReportDTO) {
        SalesReport report = salesReportRepository.findById(id)
                .orElseThrow(() -> new ReportException("Sales Report not found"));

        report.setWritten(salesReportDTO.getWritten());
        report.setAllowed(salesReportDTO.getAllowed());
        report.setSold(salesReportDTO.getSold());

        salesReportRepository.save(report);
    }

    public void editMedicineQuantity(Long quantityId, Long correction) {
        // Since we're now using MedicineWithQuantityDoctorV2, the correction field has moved to ContractMedicineDoctorAmountV2
        // This method would need to be refactored based on business requirements
        // For now, we can retrieve the entity and set a correction on the most recent amount record
        var medicineWithQuantityDoctor = medicineWithQuantityDoctorV2Repository.findById(quantityId)
                .orElseThrow(() -> new ReportException("Medicine quantity not found"));

        if (!medicineWithQuantityDoctor.getContractMedicineDoctorAmountV2s().isEmpty()) {
            var latestAmount = medicineWithQuantityDoctor.getContractMedicineDoctorAmountV2s()
                    .stream()
                    .sorted((a1, a2) -> a2.getYearMonth().compareTo(a1.getYearMonth()))
                    .findFirst()
                    .orElseThrow(() -> new ReportException("No amount records found"));
            latestAmount.setCorrection(correction);
        }

        medicineWithQuantityDoctorV2Repository.save(medicineWithQuantityDoctor);
    }

    /**
     * Converts and saves a Sales record to 5 SalesReport records, one for each ContractType
     *
     * @param sales The Sales object containing the data to be saved
     * @return The last saved SalesReport (RECIPE type)
     */
    public SalesReport saveSalesReport(Sales sales) {
        SalesReport lastSavedReport = null;
        LocalDate reportDate = LocalDate.now();
        Medicine medicine = sales.getMedicine();
        Long regionId = sales.getRegion().getId();
        YearMonth yearMonth = sales.getYearMonth();
    
        // Delete existing sales reports for this medicine, region, and month
        List<SalesReport> existingReports = salesReportRepository.findByMedicineIdAndRegionIdAndYearMonth(
                medicine.getId(), regionId, yearMonth);
        if (!existingReports.isEmpty()) {
            for (SalesReport report : existingReports) {
                report.setRegion(null);
                report.setMedicine(null);
            }
            salesReportRepository.saveAll(existingReports);
            salesReportRepository.deleteAll(existingReports);
        }
    
        // Create KZ report
        SalesReport kzReport = new SalesReport();
        kzReport.setMedicine(medicine);
        kzReport.setRegion(sales.getRegion());
        kzReport.setYearMonth(yearMonth);
        kzReport.setReportDate(reportDate);
        kzReport.setContractType(ContractType.KZ);
        Long kzAllowed = calculateAllowed(sales.getQuote(), medicine.getKbPercentage());
        kzReport.setAllowed(kzAllowed);
        kzReport.setWritten(calculateWritten(sales.getTotal(), medicine.getKbPercentage()));
        salesReportRepository.save(kzReport);
    
        // Create SU report
        SalesReport suReport = new SalesReport();
        suReport.setMedicine(medicine);
        suReport.setRegion(sales.getRegion());
        suReport.setYearMonth(yearMonth);
        suReport.setReportDate(reportDate);
        suReport.setContractType(ContractType.SU);
        Long suAllowed = calculateAllowed(sales.getQuote(), medicine.getSuPercentage());
        suReport.setAllowed(suAllowed);
        suReport.setWritten(calculateWritten(sales.getTotal(), medicine.getSuPercentage()));
        salesReportRepository.save(suReport);
    
        // Create SB report
        SalesReport sbReport = new SalesReport();
        sbReport.setMedicine(medicine);
        sbReport.setRegion(sales.getRegion());
        sbReport.setYearMonth(yearMonth);
        sbReport.setReportDate(reportDate);
        sbReport.setContractType(ContractType.SB);
        Long sbAllowed = calculateAllowed(sales.getQuote(), medicine.getSbPercentage());
        sbReport.setAllowed(sbAllowed);
        sbReport.setWritten(calculateWritten(sales.getTotal(), medicine.getSbPercentage()));
        salesReportRepository.save(sbReport);
    
        // Create GZ report
        SalesReport gzReport = new SalesReport();
        gzReport.setMedicine(medicine);
        gzReport.setRegion(sales.getRegion());
        gzReport.setYearMonth(yearMonth);
        gzReport.setReportDate(reportDate);
        gzReport.setContractType(ContractType.GZ);
        Long gzAllowed = calculateAllowed(sales.getQuote(), medicine.getGzPercentage());
        gzReport.setAllowed(gzAllowed);
        gzReport.setWritten(calculateWritten(sales.getTotal(), medicine.getGzPercentage()));
        salesReportRepository.save(gzReport);
    
        // Create RECIPE report
        SalesReport recipeReport = new SalesReport();
        recipeReport.setMedicine(medicine);
        recipeReport.setRegion(sales.getRegion());
        recipeReport.setYearMonth(yearMonth);
        recipeReport.setReportDate(reportDate);
        recipeReport.setContractType(ContractType.RECIPE);
        Long recipeAllowed = calculateAllowed(sales.getQuote(), medicine.getRecipePercentage());
        recipeReport.setAllowed(recipeAllowed);
        recipeReport.setWritten(calculateWritten(sales.getTotal(), medicine.getRecipePercentage()));
        lastSavedReport = salesReportRepository.save(recipeReport);

        return lastSavedReport;
    }

    /**
     * Calculates the allowed value based on quote and percentage
     *
     * @param quote      The base quote value
     * @param percentage The percentage to apply
     * @return The calculated allowed value
     */
    private Long calculateAllowed(Long quote, Double percentage) {
        if (quote == null || percentage == null) {
            return 0L;
        }
        return Math.round(quote * percentage / 100);
    }

    /**
     * Calculates the written value based on total and percentage
     *
     * @param total      The base total value
     * @param percentage The percentage to apply
     * @return The calculated written value
     */
    private Long calculateWritten(Long total, Double percentage) {
        if (total == null || percentage == null) {
            return 0L;
        }
        return Math.round(total * percentage / 100);
    }

    /**
     * Calculates the sold value based on allowed value and ball
     *
     * @param allowed The allowed value
     * @param ball    The ball value from Medicine
     * @return The calculated sold value
     */
    private Long calculateSold(Long allowed, Integer ball) {
        if (allowed == null || ball == null) {
            return 0L;
        }
        return Math.round(allowed * ball / 100.0);
    }


    /**
     * Converts a DoctorContractV2 entity to a ContractDTOV2 data transfer object
     *
     * @param contract the DoctorContractV2 entity to convert
     * @return a ContractDTOV2 object representing the contract
     */
    public ContractDTOV2 convertToContractDTOV2(DoctorContractV2 contract) {
        if (contract == null) {
            throw new ReportException("Contract cannot be null");
        }

        ContractDTOV2 dto = new ContractDTOV2();
        dto.setId(contract.getId());
        dto.setCreatorId(contract.getCreatedBy() != null ? contract.getCreatedBy().getUserId() : null);
        dto.setDoctorId(contract.getDoctor() != null ? contract.getDoctor().getUserId() : null);
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setStatus(contract.getStatus());
        dto.setContractType(contract.getContractType());

        if (contract.getMedicineWithQuantityDoctorV2s() != null) {
            List<MedicineQuoteDTOV2> medicineQuoteDTOs = contract.getMedicineWithQuantityDoctorV2s().stream()
                    .filter(Objects::nonNull)
                    .map(med -> {
                        MedicineQuoteDTOV2 medicineDto = new MedicineQuoteDTOV2();
                        medicineDto.setId(med.getId());
                        medicineDto.setMedicine(med.getMedicine());
                        medicineDto.setQuote(med.getQuote());

                        // Get amount from the most recent amount record if available
                        Long amount = 0L;
                        YearMonth latestYearMonth = YearMonth.now();

                        if (med.getContractMedicineDoctorAmountV2s() != null && !med.getContractMedicineDoctorAmountV2s().isEmpty()) {
                            var latestAmount = med.getContractMedicineDoctorAmountV2s().stream()
                                    .sorted((a1, a2) -> a2.getYearMonth().compareTo(a1.getYearMonth()))
                                    .findFirst()
                                    .orElse(null);

                            if (latestAmount != null) {
                                amount = latestAmount.getAmount();
                                latestYearMonth = latestAmount.getYearMonth();
                            }
                        }

                        medicineDto.setAmount(amount);
                        medicineDto.setYearMonth(latestYearMonth);

                        return medicineDto;
                    })
                    .collect(Collectors.toList());

            dto.setMedicineQuoteDTOV2List(medicineQuoteDTOs);
        }

        return dto;
    }

    public void updateSalesReport(Sales sales) {
        SalesReport lastUpdatedReport = null;
        LocalDate reportDate = LocalDate.now();
        Medicine medicine = sales.getMedicine();
        YearMonth yearMonth = sales.getYearMonth();
        Long regionId = sales.getRegion().getId();

        // Update KZ report
        SalesReport kzReport = salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.KZ,
                yearMonth
        ).orElseGet(() -> salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.KZ,
                yearMonth.minusMonths(1)
        ).orElseThrow(() -> new ReportException("KZ SalesReport not found for medicine " + medicine.getId())));
        Long kzAllowed = calculateAllowed(sales.getQuote(), medicine.getKbPercentage());
        kzReport.setAllowed(kzAllowed);
        kzReport.setWritten(calculateWritten(sales.getTotal(), medicine.getKbPercentage()));
        kzReport.setSold(calculateSold(kzAllowed, medicine.getKbBall()));
        kzReport.setReportDate(reportDate);
        salesReportRepository.save(kzReport);

        // Update SU report
        SalesReport suReport = salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.SU,
                yearMonth
        ).orElseGet(() -> salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.SU,
                yearMonth.minusMonths(1)
        ).orElseThrow(() -> new ReportException("SU SalesReport not found for medicine " + medicine.getId())));
        Long suAllowed = calculateAllowed(sales.getQuote(), medicine.getSuPercentage());
        suReport.setAllowed(suAllowed);
        suReport.setWritten(calculateWritten(sales.getTotal(), medicine.getSuPercentage()));
        suReport.setSold(calculateSold(suAllowed, medicine.getSuBall()));
        suReport.setReportDate(reportDate);
        salesReportRepository.save(suReport);

        // Update SB report
        SalesReport sbReport = salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.SB,
                yearMonth
        ).orElseGet(() -> salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.SB,
                yearMonth.minusMonths(1)
        ).orElseThrow(() -> new ReportException("SB SalesReport not found for medicine " + medicine.getId())));
        Long sbAllowed = calculateAllowed(sales.getQuote(), medicine.getSbPercentage());
        sbReport.setAllowed(sbAllowed);
        sbReport.setWritten(calculateWritten(sales.getTotal(), medicine.getSbPercentage()));
        sbReport.setSold(calculateSold(sbAllowed, medicine.getSbBall()));
        sbReport.setReportDate(reportDate);
        salesReportRepository.save(sbReport);

        // Update GZ report
        SalesReport gzReport = salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.GZ,
                yearMonth
        ).orElseGet(() -> salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.GZ,
                yearMonth.minusMonths(1)
        ).orElseThrow(() -> new ReportException("GZ SalesReport not found for medicine " + medicine.getId())));
        Long gzAllowed = calculateAllowed(sales.getQuote(), medicine.getGzPercentage());
        gzReport.setAllowed(gzAllowed);
        gzReport.setWritten(calculateWritten(sales.getTotal(), medicine.getGzPercentage()));
        gzReport.setSold(calculateSold(gzAllowed, medicine.getGzBall()));
        gzReport.setReportDate(reportDate);
        salesReportRepository.save(gzReport);

        // Update RECIPE report
        SalesReport recipeReport = salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.RECIPE,
                yearMonth
        ).orElseGet(() -> salesReportRepository.findByFilters(
                medicine.getId(),
                regionId,
                ContractType.RECIPE,
                yearMonth.minusMonths(1)
        ).orElseThrow(() -> new ReportException("RECIPE SalesReport not found for medicine " + medicine.getId())));
        Long recipeAllowed = calculateAllowed(sales.getQuote(), medicine.getRecipePercentage());
        recipeReport.setAllowed(recipeAllowed);
        recipeReport.setWritten(calculateWritten(sales.getTotal(), medicine.getRecipePercentage()));
        recipeReport.setSold(calculateSold(recipeAllowed, medicine.getRecipeBall()));
        recipeReport.setReportDate(reportDate);
        salesReportRepository.save(recipeReport);
    }

    public List<SalesReportDTO> getSalesReportDTOList(ContractType contractType, Long regionId, YearMonth yearMonth) {
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate();
        List<SalesReportDTO> salesReportDTOS = new ArrayList<>();
        salesRepository.findAllByYearMonth(yearMonth);

        for (Medicine medicine : medicines) {
            SalesReportDTO salesReportDTO = new SalesReportDTO();

            SalesReport salesReport = salesReportRepository.findByFilters(
                    medicine.getId(),
                    regionId,
                    contractType,
                    yearMonth
            ).orElseGet(() -> salesReportRepository.findByFilters(
                    medicine.getId(),
                    regionId,
                    contractType,
                    yearMonth.minusMonths(1)
            ).orElse(null));
            if (salesReport == null) {
                continue;
            }
            salesReportDTO.setId(salesReport.getId());
            salesReportDTO.setAllowed(salesReport.getAllowed());
            salesReportDTO.setWritten(salesReport.getWritten());
            Long inFact = medicineWithQuantityDoctorV2Repository.findTotalWrittenInFact(medicine.getId(), contractType, regionId, null,null,null, yearMonth);
            salesReportDTO.setSold(inFact);
            salesReportDTO.setMedicineId(medicine.getId());
            salesReportDTO.setMedicine(medicine);
            salesReportDTO.setContractType(contractType);

            salesReportDTOS.add(salesReportDTO);
        }
        return salesReportDTOS;
    }

    public SalesReportDTO getFieldForceSalesReportsByFilters(List<Long> regionIds, ContractType contractType, Long medicineId, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, YearMonth yearMonth) {
        SalesReportDTO dto = new SalesReportDTO();

        // Try current month
        List<SalesReport> results = salesReportRepository.findByFilters(
                regionIds,
                contractType,
                medicineId,
                regionId,
                yearMonth
        );

        // If empty, try previous month
        if (results.isEmpty()) {
            results = salesReportRepository.findByFilters(
                    regionIds,
                    contractType,
                    medicineId,
                    regionId,
                    yearMonth.minusMonths(1)
            );
        }

        SalesReport report = results.isEmpty() ? null : results.get(0);
        if (report == null) {
            throw new ReportException("Report not found");
        }

        dto.setId(report.getId());
        // Calculate totals using V2 repositories across all regions
        Long written = 0L;
        Long allowed = 0L;
        for (Long rId : regionIds) {
            written += medicineWithQuantityDoctorV2Repository.findTotalWritten(medicineId, contractType, rId, districtId, workplaceId, fieldName, yearMonth);
            allowed += medicineWithQuantityDoctorV2Repository.findTotalAllowed(medicineId, contractType, rId, districtId, workplaceId, fieldName, yearMonth);
        }

        dto.setWritten(written);
        dto.setAllowed(allowed);
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setMedicine(report.getMedicine());
        dto.setId(report.getId());
        dto.setContractType(contractType);
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(regionIds, contractType, medicineId, query, regionId, districtId, workplaceId, fieldName, yearMonth));

        return dto;
    }

    public SalesReportDTO getFieldForceSalesReportsByFilters(List<Long> regionIds, ContractType contractType, Long medicineId, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, LocalDate startDate, LocalDate endDate) {
        SalesReportDTO dto = new SalesReportDTO();

        // Convert LocalDate to YearMonth for the previous month fallback
        YearMonth currentYearMonth = YearMonth.from(startDate);

        // First try with date range
        List<SalesReport> results = salesReportRepository.findByDateRange(
                regionIds,
                contractType,
                medicineId,
                regionId,
                startDate,
                endDate
        );

        // If empty, try previous month
        if (results.isEmpty()) {
            results = salesReportRepository.findByFilters(
                    regionIds,
                    contractType,
                    medicineId,
                    regionId,
                    currentYearMonth.minusMonths(1)
            );
        }

        SalesReport report = results.isEmpty() ? null : results.get(0);
        if (report == null) {
            throw new ReportException("Report not found");
        }

        dto.setId(report.getId());
        // Calculate totals using V2 repositories across all regions
        Long written = 0L;
        Long allowed = 0L;
        for (Long rId : regionIds) {
            written += medicineWithQuantityDoctorV2Repository.findTotalWritten(medicineId, contractType, rId, startDate, endDate);
            allowed += medicineWithQuantityDoctorV2Repository.findTotalAllowed(medicineId, contractType, rId, startDate, endDate);
        }

        dto.setWritten(written);
        dto.setAllowed(allowed);
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setMedicine(report.getMedicine());
        dto.setId(report.getId());
        dto.setContractType(contractType);
        // Convert the date to YearMonth for compatibility with doctor reports
        YearMonth yearMonth = report.getYearMonth();
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(regionIds, contractType, medicineId, query, regionId, districtId, workplaceId, fieldName, yearMonth));

        return dto;
    }

    public List<AdminReportDTO> getAdminReportDTOListFilters(Long regionId, YearMonth yearMonth) {
        List<AdminReportDTO> result = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate();

        for (Medicine medicine : medicines) {
            Long medicineId = medicine.getId();

            Long recipe = salesReportRepository.countRecipeByMedicine(medicineId, regionId, yearMonth);
            Long su = salesReportRepository.countSUByMedicine(medicineId, regionId, yearMonth);
            Long sb = salesReportRepository.countSBByMedicine(medicineId, regionId, yearMonth);
            Long gz = salesReportRepository.countGZByMedicine(medicineId, regionId, yearMonth);
            Long kb = salesReportRepository.countKZByMedicine(medicineId, regionId, yearMonth);

            // Use V2 repository to get allowed count
            Long allowed = medicineWithQuantityDoctorV2Repository.findTotalAllowed(
                    medicineId,
                    null, // contractType - using null to get all types
                    regionId,
                    null, // districtId
                    null, // workplaceId
                    null, // fieldName
                    yearMonth
            );

            Long total = recipe + su + sb + gz + kb;
            Long percentage = total > 0 && allowed > 0 ? (total * 100) / allowed : 0;

            AdminReportDTO dto = new AdminReportDTO();
            dto.setMedicineId(medicineId);
            dto.setRecipe(recipe);
            dto.setSu(su);
            dto.setSb(sb);
            dto.setGz(gz);
            dto.setKb(kb);
            dto.setAllowed(allowed);
            dto.setMedicine(medicine);
            dto.setPercentage(percentage);

            result.add(dto);
        }

        return result;
    }

    public List<AdminReportDTO> getAdminReportDTOListFilters(Long regionId, LocalDate startDate, LocalDate endDate) {
        List<AdminReportDTO> result = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate();

        for (Medicine medicine : medicines) {
            Long medicineId = medicine.getId();

            // Use date range version of the count queries
            Long recipe = salesReportRepository.countRecipeByDateRange(medicineId, regionId, null, startDate, endDate);
            Long su = salesReportRepository.countSUByDateRange(medicineId, regionId, null, startDate, endDate);
            Long sb = salesReportRepository.countSBByDateRange(medicineId, regionId, null, startDate, endDate);
            Long gz = salesReportRepository.countGZByDateRange(medicineId, regionId, null, startDate, endDate);
            Long kb = salesReportRepository.countKZByDateRange(medicineId, regionId, null, startDate, endDate);

            // Use V2 repository for allowed count
            Long allowed = medicineWithQuantityDoctorV2Repository.findTotalAllowed(
                    medicineId,
                    null, // contractType - using null to get all types
                    regionId,
                    startDate,
                    endDate
            );

            Long total = recipe + su + sb + gz + kb;
            Long percentage = total > 0 && allowed > 0 ? (total * 100) / allowed : 0;

            AdminReportDTO dto = new AdminReportDTO();
            dto.setMedicineId(medicineId);
            dto.setRecipe(recipe);
            dto.setSu(su);
            dto.setSb(sb);
            dto.setGz(gz);
            dto.setKb(kb);
            dto.setAllowed(allowed);
            dto.setMedicine(medicine);
            dto.setPercentage(percentage);

            result.add(dto);
        }

        return result;
    }

    public List<AdminReportDTO> getFieldForceReports(List<Long> regionIds, Long regionId, YearMonth yearMonth) {
        List<AdminReportDTO> reportDTOList = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate();

        for (Medicine medicine : medicines) {
            Long medicineId = medicine.getId();

            Long recipe = salesReportRepository.countRecipeByFilters(medicineId, regionId, regionIds, yearMonth);
            Long su = salesReportRepository.countSUByFilters(medicineId, regionId, regionIds, yearMonth);
            Long sb = salesReportRepository.countSBByFilters(medicineId, regionId, regionIds, yearMonth);
            Long gz = salesReportRepository.countGZByFilters(medicineId, regionId, regionIds, yearMonth);
            Long kb = salesReportRepository.countKZByFilters(medicineId, regionId, regionIds, yearMonth);
            Long allowed = salesReportRepository.countAllowedByFilters(medicineId, regionId, regionIds, yearMonth);

            Long total = recipe + su + sb + gz + kb;
            Long percentage = total > 0 ? (total * 100) / allowed : 0;

            AdminReportDTO dto = new AdminReportDTO();
            dto.setMedicineId(medicineId);
            dto.setRecipe(recipe);
            dto.setSu(su);
            dto.setSb(sb);
            dto.setGz(gz);
            dto.setKb(kb);
            dto.setPercentage(percentage);
            dto.setAllowed(allowed);
            dto.setMedicine(medicine);

            reportDTOList.add(dto);
        }

        return reportDTOList;
    }

    public List<AdminReportDTO> getFieldForceReports(List<Long> regionIds, Long regionId, LocalDate startDate, LocalDate endDate) {
        List<AdminReportDTO> reportDTOList = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate();

        for (Medicine medicine : medicines) {
            Long medicineId = medicine.getId();

            Long recipe = salesReportRepository.countRecipeByDateRange(medicineId, regionId, regionIds, startDate, endDate);
            Long su = salesReportRepository.countSUByDateRange(medicineId, regionId, regionIds, startDate, endDate);
            Long sb = salesReportRepository.countSBByDateRange(medicineId, regionId, regionIds, startDate, endDate);
            Long gz = salesReportRepository.countGZByDateRange(medicineId, regionId, regionIds, startDate, endDate);
            Long kb = salesReportRepository.countKZByDateRange(medicineId, regionId, regionIds, startDate, endDate);
            Long allowed = salesReportRepository.countAllowedByDateRange(medicineId, regionId, regionIds, startDate, endDate);

            Long total = recipe + su + sb + gz + kb;
            Long percentage = total > 0 && allowed > 0 ? (total * 100) / allowed : 0;

            AdminReportDTO dto = new AdminReportDTO();
            dto.setMedicineId(medicineId);
            dto.setRecipe(recipe);
            dto.setSu(su);
            dto.setSb(sb);
            dto.setGz(gz);
            dto.setKb(kb);
            dto.setPercentage(percentage);
            dto.setAllowed(allowed);
            dto.setMedicine(medicine);

            reportDTOList.add(dto);
        }

        return reportDTOList;
    }
}
