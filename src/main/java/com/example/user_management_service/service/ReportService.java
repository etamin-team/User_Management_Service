package com.example.user_management_service.service;

import com.example.user_management_service.exception.ReportException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        SalesReport report = salesReportRepository.findByFilters(medicineId, regionId, startDate, endDate).orElseThrow(()->new ReportException("Report not found"));
        dto.setId(report.getId());
        dto.setWritten(report.getWritten());
        dto.setAllowed(report.getAllowed());
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(medicineId,query,regionId,districtId,workplaceId,fieldName));

        return dto;
    }


    public DoctorReportDTO getDoctorReports(Long medicineId,ContractType contractType, String query, Long regionId, Long districtId, Long workplaceId, LocalDate startDate, LocalDate endDate, Field fieldName) {
        DoctorReportDTO doctorReportDTO = new DoctorReportDTO();
        Long allowed = contractRepository.findTotalAllowed(medicineId,contractType,query, regionId, districtId, workplaceId, fieldName,startDate,endDate);
        Long written = contractRepository.findTotalWritten(medicineId,contractType,query, regionId, districtId, workplaceId, fieldName,startDate,endDate);
        Long inFact = contractRepository.findTotalWrittenInFact(medicineId,contractType,query,regionId, districtId, workplaceId, fieldName,startDate,endDate);

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

    public List<SalesReportDTO> getSalesReportDTOList(ContractType contractType,String query, Long regionId, Long districtId, Long workplaceId, LocalDate startDate, LocalDate endDate, Field fieldName) {
        List<Medicine> medicines=medicineRepository.findAllSortByCreatedDate();
        List<SalesReportDTO> salesReportDTOS=new ArrayList<>();
        for (Medicine medicine:medicines) {
            SalesReportDTO salesReportDTO;
            SalesReport salesReport=salesReportRepository.findByFilters(medicine.getId(),regionId, startDate, endDate).orElse(null);
            if (salesReport==null){
                salesReportDTO=new SalesReportDTO();
                Long allowed = contractRepository.findTotalAllowed(medicine.getId(),contractType,query, regionId, districtId, workplaceId, fieldName,startDate,endDate);
                Long written = contractRepository.findTotalWritten(medicine.getId(),contractType,query, regionId, districtId, workplaceId, fieldName,startDate,endDate);
                Long inFact = contractRepository.findTotalWrittenInFact(medicine.getId(),contractType,query,regionId, districtId, workplaceId, fieldName,startDate,endDate);

                salesReportDTO.setAllowed(allowed);
                salesReportDTO.setWritten(written);
                salesReportDTO.setSold(inFact);
                salesReportDTO.setMedicineId(medicine.getId());
                salesReportDTO.setContractType(contractType);
            }else {
                salesReportDTO=new SalesReportDTO();
                salesReportDTO.setAllowed(salesReport.getAllowed());
                salesReportDTO.setWritten(salesReport.getWritten());
                salesReportDTO.setSold(salesReport.getSold());
                salesReportDTO.setMedicineId(medicine.getId());
                salesReportDTO.setContractType(contractType);
            }
            salesReportDTOS.add(salesReportDTO);
        }
        return salesReportDTOS;
    }

    public SalesReportDTO getFieldForceSalesReportsByFilters(List<Long> regionIds, Long medicineId, String query, Long regionId, Long districtId, Long workplaceId, Field fieldName, LocalDate startDate, LocalDate endDate) {

        SalesReportDTO dto =  new SalesReportDTO();
        SalesReport report = salesReportRepository.findByFilters(regionIds,medicineId, regionId, startDate, endDate).orElseThrow(()->new ReportException("Report not found"));
        dto.setId(report.getId());
        dto.setWritten(report.getWritten());
        dto.setAllowed(report.getAllowed());
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(regionIds,medicineId,query,regionId,districtId,workplaceId,fieldName));

        return dto;

    }
}
