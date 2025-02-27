package com.example.user_management_service.service;

import com.example.user_management_service.exception.ReportException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.ContractRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.MedicineWithQuantityDoctorRepository;
import com.example.user_management_service.repository.SalesReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Date-2/23/2025
 * By Sardor Tokhirov
 * Time-3:24 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ContractRepository contractRepository;
    private final UserService userService;
    private final ContractService contractService;
    private final SalesReportRepository salesReportRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;

    public List<SalesReportDTO> getSalesReportsByFilters(Long medicineId, String query, Long districtId, Long workplaceId, Field fieldName) {
        List<Contract> contracts = contractRepository.findContractsByFilters(medicineId, query, districtId, workplaceId, fieldName);

        return contracts.stream().map(contract -> {
            SalesReportDTO dto = new SalesReportDTO();
            dto.setMedicineId(medicineId);
            dto.setId(contract.getId());

            // Fetch related SalesReport
            SalesReport report = salesReportRepository.findByMedicineId(medicineId)
                    .orElseThrow(() -> new ReportException("Sales Report not found"));

            dto.setWritten(report.getWritten());
            dto.setAllowed(report.getAllowed());
            dto.setSold(report.getSold());

            // Fetch related MedicineWithQuantityDoctors
            List<MedicineWithQuantityDTO> medicineList = medicineWithQuantityDoctorRepository
                    .findByMedicineId(medicineId)
                    .stream()
                    .map(medicine -> new MedicineWithQuantityDTO(medicine.getId(),medicineId,medicine.getQuote(),medicine.getCorrection(),null,medicine.getContractMedicineDoctorAmount(), medicine.getMedicine()))
                    .collect(Collectors.toList());

            dto.setMedicineWithQuantityDoctors(medicineList);

            return dto;
        }).collect(Collectors.toList());
    }




    public DoctorReportDTO getDoctorReports( Long medicineId, String query, Long districtId, Long workplaceId, Field fieldName) {
        DoctorReportDTO doctorReportDTO = new DoctorReportDTO();
        Long allowed = contractRepository.findTotalAllowed(medicineId,query, districtId, workplaceId, fieldName);
        Long written = contractRepository.findTotalWritten(medicineId,query, districtId, workplaceId, fieldName);
        Long inFact = contractRepository.findTotalWrittenInFact(medicineId,query, districtId, workplaceId, fieldName);

        doctorReportDTO.setAllowed(allowed);
        doctorReportDTO.setWritten(written);
        doctorReportDTO.setWrittenInFact(inFact);
        doctorReportDTO.setDoctorReportListDTOList(getDoctorReportListDTOList(medicineId,query,districtId,workplaceId,fieldName));

        return doctorReportDTO;
    }
    public List<DoctorReportListDTO> getDoctorReportListDTOList(Long medicineId,String query, Long districtId, Long workplaceId, Field fieldName) {
        List<Contract> contracts = contractRepository.findContractsByFilters(medicineId,query, districtId, workplaceId, fieldName);

        return contracts.stream()
                .map(contract -> {
                    DoctorReportListDTO dto = new DoctorReportListDTO();
                    dto.setDoctor(userService.convertToDTO(contract.getDoctor()));
                    dto.setContractDTO(contractService.convertToDTO(contract));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveSalesReports(SalesReportListDTO salesReportListDTO) {
        for (SalesReportDTO dto : salesReportListDTO.getSalesReportDTOS()) {
            SalesReport report = new SalesReport();
            report.setReportDate(salesReportListDTO.getDate());
            report.setWritten(dto.getWritten());
            report.setAllowed(dto.getAllowed());
            report.setSold(dto.getSold());
            dto.getMedicineWithQuantityDoctors()
                    .stream()
                    .forEach(this::saveQualities);

            Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                    .orElseThrow(() -> new ReportException("Medicine not found"));
            report.setMedicine(medicine);

            salesReportRepository.save(report);
        }
    }

    private void saveQualities(MedicineWithQuantityDTO medicineWithQuantityDoctors) {
        MedicineWithQuantityDoctor medicineWithQuantityDoctor = medicineWithQuantityDoctorRepository.findById(medicineWithQuantityDoctors.getQuantityId()).orElseThrow(()->new ReportException("Not found"));
        medicineWithQuantityDoctor.setCorrection(medicineWithQuantityDoctors.getCorrection());
        medicineWithQuantityDoctorRepository.save(medicineWithQuantityDoctor);
    }

    @Transactional
    public void editSalesReport(Long id, SalesReportDTO salesReportDTO) {
        SalesReport report = salesReportRepository.findById(id)
                .orElseThrow(() -> new ReportException("Sales Report not found"));

        report.setWritten(salesReportDTO.getWritten());
        report.setAllowed(salesReportDTO.getAllowed());
        report.setSold(salesReportDTO.getSold());

        salesReportRepository.save(report);
    }
    @Transactional
    public void editMedicineQuantity(Long quantityId, Long correction) {
        MedicineWithQuantityDoctor  medicineWithQuantityDoctor=medicineWithQuantityDoctorRepository.findById(quantityId).orElseThrow(() -> new ReportException("Sales Report not found"));
        medicineWithQuantityDoctor.setCorrection(correction);
        medicineWithQuantityDoctorRepository.save(medicineWithQuantityDoctor);

    }
}
