package com.example.user_management_service.service;

import com.example.user_management_service.model.Contract;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.dto.DoctorReportDTO;
import com.example.user_management_service.model.dto.DoctorReportListDTO;
import com.example.user_management_service.repository.ContractRepository;
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

    public DoctorReportDTO getDoctorReports(String query, Long districtId, Long workplaceId, Field fieldName) {
        DoctorReportDTO doctorReportDTO = new DoctorReportDTO();
        Long allowed = contractRepository.findTotalAllowed(query, districtId, workplaceId, fieldName);
        Long written = contractRepository.findTotalWritten(query, districtId, workplaceId, fieldName);

        doctorReportDTO.setAllowed(allowed);
        doctorReportDTO.setWritten(written);
        doctorReportDTO.setDoctorReportListDTOList(getDoctorReportListDTOList(query,districtId,workplaceId,fieldName));

        return doctorReportDTO;
    }
    public List<DoctorReportListDTO> getDoctorReportListDTOList(String query, Long districtId, Long workplaceId, Field fieldName) {
        List<Contract> contracts = contractRepository.findContractsByFilters(query, districtId, workplaceId, fieldName);

        return contracts.stream()
                .map(contract -> {
                    DoctorReportListDTO dto = new DoctorReportListDTO();
                    dto.setDoctor(userService.convertToDTO(contract.getDoctor()));
                    dto.setContractDTO(contractService.convertToDTO(contract));
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
