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

        System.out.println("11111111111111111111111111");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        SalesReportDTO dto =  new SalesReportDTO();
        SalesReport report = salesReportRepository.findByFilters(medicineId, regionId, startDate, endDate).orElseThrow(()->new ReportException("Report not found"));
        System.out.println("11111111111111111111111111");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        dto.setId(report.getId());
        dto.setWritten(report.getWritten());
        dto.setAllowed(report.getAllowed());
        dto.setSold(report.getSold());
        dto.setMedicineId(medicineId);
        dto.setDoctorReportListDTOS(getDoctorReportListDTOList(medicineId,query,regionId,districtId,workplaceId,fieldName));

        return dto;
    }




    public DoctorReportDTO getDoctorReports( Long medicineId, String query,Long regionId,  Long districtId, Long workplaceId, Field fieldName) {
        DoctorReportDTO doctorReportDTO = new DoctorReportDTO();
        Long allowed = contractRepository.findTotalAllowed(medicineId,query, regionId, districtId, workplaceId, fieldName);
        Long written = contractRepository.findTotalWritten(medicineId,query,regionId, districtId,workplaceId, fieldName);
        Long inFact = contractRepository.findTotalWrittenInFact(medicineId,query,regionId, districtId, workplaceId, fieldName);

        doctorReportDTO.setAllowed(allowed);
        doctorReportDTO.setWritten(written);
        doctorReportDTO.setWrittenInFact(inFact);
        doctorReportDTO.setDoctorReportListDTOList(getDoctorReportListDTOList(medicineId,query,regionId,districtId,workplaceId,fieldName));

        return doctorReportDTO;
    }
    public List<DoctorReportListDTO> getDoctorReportListDTOList(Long medicineId,String query, Long regionId, Long districtId, Long workplaceId, Field fieldName) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println("---------------------------------------------------");
        System.out.println("---------------------------------------------------");
        System.out.println("---------------------------------------------------");
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

    public List<DoctorReportDTO> getDoctorReportsList(String query, Long regionId, Long districtId, Long workplaceId, Field fieldName) {
        List<Medicine> medicines=medicineRepository.findAllSortByCreatedDate();
        List<DoctorReportDTO> doctorReportDTOS=new ArrayList<>();
        for (Medicine medicine:medicines) {
            DoctorReportDTO dto=getDoctorReports(medicine.getId(),query,regionId,districtId,workplaceId,fieldName);
            dto.setMedicine(medicine);
            doctorReportDTOS.add(dto);
        }
        return doctorReportDTOS;
    }
}
