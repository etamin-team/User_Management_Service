package com.example.user_management_service.service;

import com.example.user_management_service.exception.ReportException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private final MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;
    private final RegionRepository regionRepository;
    private final SalesService salesService;
    private final RecipeRepository recipeRepository;

    public SalesReportDTO getSalesReportsByFilters(Long medicineId, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, LocalDate startDate, LocalDate endDate) {

        SalesReportDTO dto =  new SalesReportDTO();

        List<SalesReport> reports = salesReportRepository.findByFilters(
                medicineId, regionId
        );

        if (reports.isEmpty()) {
            throw new ReportException("Report not found");
        }

        SalesReport report = reports.get(0); // this is your first and only record  dto.setId(report.getId());
        dto.setWritten(report.getWritten());
        dto.setAllowed(report.getAllowed());
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(medicineId,query,regionId,districtId,workplaceId,fieldName));

        return dto;
    }


    public DoctorReportDTO getDoctorReports(Long medicineId,ContractType contractType, String query, Long regionId, Long districtId, Long workplaceId, LocalDate startDate, LocalDate endDate, Field fieldName) {
        DoctorReportDTO doctorReportDTO = new DoctorReportDTO();
        Long allowed = medicineWithQuantityDoctorRepository.findTotalAllowed(medicineId,contractType,regionId, districtId, workplaceId, fieldName);
        Long written = medicineWithQuantityDoctorRepository.findTotalWritten(medicineId,contractType, regionId, districtId, workplaceId, fieldName);
        Long inFact = medicineWithQuantityDoctorRepository.findTotalWrittenInFact(medicineId,contractType,regionId, districtId, workplaceId, fieldName);

        doctorReportDTO.setAllowed(allowed);
        doctorReportDTO.setWritten(written);
        doctorReportDTO.setWrittenInFact(inFact);
        doctorReportDTO.setDoctorReportListDTOList(getDoctorReportListDTOList(medicineId,query,regionId,districtId,workplaceId,fieldName));

        return doctorReportDTO;
    }
    public List<DoctorReportListDTO> getDoctorReportListDTOList(Long medicineId,String query, Long regionId, Long districtId, Long workplaceId, Field fieldName) {
        List<Contract> contracts = contractRepository.findContractsByFilters(medicineId,query,regionId, districtId, workplaceId, fieldName);

        return contracts.stream()
                .map(contract -> {
                    DoctorReportListDTO dto = new DoctorReportListDTO();
                    dto.setDoctor(userService.convertToDTO(contract.getDoctor()));
                    dto.setContractDTO(contractService.convertToDTO(contract));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DoctorReportListDTO> getDoctorReportListDTOList(List<Long> regionIds,Long medicineId,String query, Long regionId, Long districtId, Long workplaceId, Field fieldName) {
        List<Contract> contracts = contractRepository.findContractsByFilters(regionIds,medicineId,query,regionId, districtId, workplaceId, fieldName);

        return contracts.stream()
                .map(contract -> {
                    DoctorReportListDTO dto = new DoctorReportListDTO();
                    dto.setDoctor(userService.convertToDTO(contract.getDoctor()));
                    dto.setContractDTO(contractService.convertToDTO(contract));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public void saveSalesReports(SalesReportListDTO salesReportListDTO) {
        for (SalesReportDTO dto : salesReportListDTO.getSalesReportDTOS()) {
            SalesReport report = new SalesReport();
            report.setReportDate(salesReportListDTO.getDate());
            report.setWritten(dto.getWritten());
            report.setAllowed(dto.getAllowed());
            report.setSold(dto.getSold());
            report.setStartDate(salesReportListDTO.getStartDate());
            report.setEndDate(salesReportListDTO.getEndDate());
            report.setContractType(dto.getContractType());


            Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                    .orElseThrow(() -> new ReportException("Medicine not found"));
            report.setMedicine(medicine);

            Region region=regionRepository.findById(salesReportListDTO.getRegionId()).orElseThrow(() -> new ReportException("Region not found"));
            report.setRegion(region);

            salesReportRepository.save(report);
            salesService.saveSalesDTO(salesReportListDTO.getStartDate(),salesReportListDTO.getEndDate(),dto,region,medicine);
        }
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
        MedicineWithQuantityDoctor  medicineWithQuantityDoctor=medicineWithQuantityDoctorRepository.findById(quantityId).orElseThrow(() -> new ReportException("Sales Report not found"));
        medicineWithQuantityDoctor.setCorrection(correction);
        medicineWithQuantityDoctorRepository.save(medicineWithQuantityDoctor);

    }

    public List<SalesReportDTO> getSalesReportDTOList(ContractType contractType, Long regionId,  LocalDate startDate, LocalDate endDate) {
        List<Medicine> medicines=medicineRepository.findAllSortByCreatedDate();
        List<SalesReportDTO> salesReportDTOS=new ArrayList<>();
        for (Medicine medicine:medicines) {
            SalesReportDTO salesReportDTO;
            System.out.println("-----------111111111");
            List<SalesReport> results=salesReportRepository.findByFilters(medicine.getId(),regionId);
            SalesReport salesReport = results.isEmpty() ? null : results.get(0); // or throw an exception if needed
            System.out.println("-----------222222222");
            if (salesReport==null){
                salesReportDTO=new SalesReportDTO();
                salesReport=new SalesReport();
//                Long allowed = contractRepository.findTotalAllowed(medicine.getId(),contractType, regionId);
//                Long written = contractRepository.findTotalWritten(medicine.getId(),contractType, regionId);
//                Long inFact = contractRepository.findTotalWrittenInFact(medicine.getId(),contractType,regionId);

                Long allowed = medicineWithQuantityDoctorRepository.findTotalAllowed(medicine.getId(),contractType, regionId);
                Long written = medicineWithQuantityDoctorRepository.findTotalWritten(medicine.getId(),contractType, regionId);
                Long inFact = medicineWithQuantityDoctorRepository.findTotalWrittenInFact(medicine.getId(),contractType,regionId);
                salesReportDTO.setAllowed(allowed);
                salesReportDTO.setWritten(written);
                salesReportDTO.setSold(inFact);
                salesReportDTO.setMedicine(medicine);
                salesReportDTO.setMedicineId(medicine.getId());
                salesReportDTO.setContractType(contractType);
                salesReport.setAllowed(allowed);
                salesReport.setWritten(written);
                salesReport.setSold(inFact);
                salesReport.setMedicine(medicine);
                salesReport.setContractType(contractType);
                SalesReport save = salesReportRepository.save(salesReport);
                salesReportDTO.setId(save.getId());
            }else {
                Long allowed = medicineWithQuantityDoctorRepository.findTotalAllowed(medicine.getId(),contractType, regionId);
                Long written = medicineWithQuantityDoctorRepository.findTotalWritten(medicine.getId(),contractType, regionId);
                salesReportDTO=new SalesReportDTO();
                salesReportDTO.setId(salesReport.getId());
                salesReportDTO.setAllowed(allowed);
                salesReportDTO.setWritten(written);
                salesReportDTO.setSold(salesReport.getSold());
                salesReportDTO.setMedicineId(medicine.getId());
                salesReportDTO.setMedicine(medicine);
                salesReportDTO.setContractType(contractType);
            }
            salesReportDTOS.add(salesReportDTO);
        }
        return salesReportDTOS;
    }

    public SalesReportDTO getFieldForceSalesReportsByFilters(List<Long> regionIds, Long medicineId, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, LocalDate startDate, LocalDate endDate) {

        SalesReportDTO dto =  new SalesReportDTO();
        List<SalesReport> results = salesReportRepository.findByFilters(
                regionIds, medicineId, regionId
        );

        SalesReport report = results.isEmpty() ? null : results.get(0); // or throw an exception if needed

        dto.setId(report.getId());
        dto.setWritten(report.getWritten());
        dto.setAllowed(report.getAllowed());
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(regionIds,medicineId,query,regionId,districtId,workplaceId,fieldName));

        return dto;

    }

    public List<AdminReportDTO> getAdminReportDTOListFilters(Long regionId, LocalDate startDate, LocalDate endDate) {
        List<AdminReportDTO> result = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate();

        for (Medicine medicine : medicines) {
            Long medicineId = medicine.getId();

            Long recipe = salesReportRepository.countRecipeByMedicine(medicineId, regionId, startDate, endDate);
            Long su = salesReportRepository.countSUByMedicine(medicineId, regionId, startDate, endDate);
            Long sb = salesReportRepository.countSBByMedicine(medicineId, regionId, startDate, endDate);
            Long gz = salesReportRepository.countGZByMedicine(medicineId, regionId, startDate, endDate);
            Long kb = salesReportRepository.countKZByMedicine(medicineId, regionId, startDate, endDate);


            Long total = recipe + su + sb + gz + kb;
            Long percentage = total > 0 ? (recipe * 100) / total : 0;

            AdminReportDTO dto = new AdminReportDTO();
            dto.setMedicineId(medicineId);
            dto.setRecipe(recipe);
            dto.setSu(su);
            dto.setSb(sb);
            dto.setGz(gz);
            dto.setKb(kb);
            dto.setMedicine(medicine);
            dto.setPercentage(percentage);

            result.add(dto);

        }

        return result;
    }

    public List<AdminReportDTO> getFieldForceReports(List<Long> regionIds, Long regionId, LocalDate startDate, LocalDate endDate) {

        List<AdminReportDTO> reportDTOList = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate();

        for (Medicine medicine : medicines) {
            Long medicineId = medicine.getId();

            Long recipe = salesReportRepository.countRecipeByFilters(medicineId, regionId, regionIds, startDate, endDate);
            Long su = salesReportRepository.countSUByFilters(medicineId, regionId, regionIds, startDate, endDate);
            Long sb = salesReportRepository.countSBByFilters(medicineId, regionId, regionIds, startDate, endDate);
            Long gz = salesReportRepository.countGZByFilters(medicineId, regionId, regionIds, startDate, endDate);
            Long kb = salesReportRepository.countKZByFilters(medicineId, regionId, regionIds, startDate, endDate);

            Long total = recipe + su + sb + gz + kb;
            Long percentage = total > 0 ? (recipe * 100) / total : 0;

            AdminReportDTO dto = new AdminReportDTO();
            dto.setMedicineId(medicineId);
            dto.setRecipe(recipe);
            dto.setSu(su);
            dto.setSb(sb);
            dto.setGz(gz);
            dto.setKb(kb);
            dto.setPercentage(percentage);
            dto.setMedicine(medicine);

            reportDTOList.add(dto);
        }

        return reportDTOList;
    }

}
