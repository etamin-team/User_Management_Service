package com.example.user_management_service.service;

import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.dto.RecordDTO;
import com.example.user_management_service.model.dto.RecordDistrictDTO;
import com.example.user_management_service.model.dto.RecordRegionDTO;
import com.example.user_management_service.model.dto.StatsEmployeeDTO;
import com.example.user_management_service.repository.ContractMedicineAmountRepository;
import com.example.user_management_service.repository.MedicineWithQuantityDoctorRepository;
import com.example.user_management_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-2/22/2025
 * By Sardor Tokhirov
 * Time-7:22 PM (GMT+5)
 */
@Service
@AllArgsConstructor
public class DashboardService {

    private MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;
    private ContractMedicineAmountRepository contractMedicineAmountRepository;
    private UserRepository userRepository;

    public RecordDTO getFilteredRecords(Long regionId, Long districtId, Long workplaceId, Field field, UUID userId, Long medicineId, LocalDate startDate, LocalDate endDate) {
//        if (medicineId != null) {
//            return filterByMedicineId(medicineId, startDate, endDate);
//        } else
            if (userId != null) {
            return filterByQuery(userId, startDate, endDate);
        } else if (field != null) {
            return filterByField(workplaceId,field, startDate, endDate);
        } else if (workplaceId != null) {
            return filterByWorkplaceId(workplaceId, startDate, endDate);
        } else if (districtId != null) {
            return filterByDistrictId(districtId, startDate, endDate);
        } else
            if (regionId != null) {
            return filterByRegionId(regionId, startDate, endDate);
        }

        RecordDTO recordDTO= new RecordDTO();
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotes());
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmount());
        List<StatsEmployeeDTO> userCountByRegion = userRepository.getUserCountByRegion();
        RecordRegionDTO recordRegionDTO= new RecordRegionDTO();
        recordRegionDTO.setEmployeeStatsList(userCountByRegion);

        recordDTO.setRecordRegionDTO(recordRegionDTO);
        return recordDTO;
    }

    private RecordDTO filterByMedicineId(Long medicineId, LocalDate startDate, LocalDate endDate) {
        // Implement logic for filtering by medicineId
        return new RecordDTO();
    }

    private RecordDTO filterByQuery(UUID userId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO= new RecordDTO();
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByUserId(userId));
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByUserId(userId));
        return recordDTO;
    }

    private RecordDTO filterByField(Long workPlaceId, Field field, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO= new RecordDTO();
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByWorkplaceAndField(workPlaceId,field.name()));
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByWorkplaceAndField(workPlaceId,field.name()));
        return recordDTO;
    }

    private RecordDTO filterByWorkplaceId(Long workplaceId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO= new RecordDTO();
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByDistrictAndWorkplace(workplaceId));
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByDistrictAndWorkplace(workplaceId));
        return recordDTO;
    }

    private RecordDTO filterByDistrictId(Long districtId, LocalDate startDate, LocalDate endDate) {
        // Implement logic for filtering by districtId
        return new RecordDTO();
    }

    private RecordDTO filterByRegionId(Long regionId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO= new RecordDTO();
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByRegion(regionId));
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByRegion(regionId));
        List<StatsEmployeeDTO> userCountByRegion = userRepository.getUserCountByDistrictInRegion(regionId);
        RecordDistrictDTO recordDistrictDTO= new RecordDistrictDTO();
        recordDistrictDTO.setEmployeeStatsList(userCountByRegion);

        recordDTO.setRecordDistrictDTO(recordDistrictDTO);
        return recordDTO;
    }

}
