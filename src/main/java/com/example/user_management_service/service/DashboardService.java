package com.example.user_management_service.service;

import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

    private final RegionRepository regionRepository;
    private final WorkPlaceRepository workPlaceRepository;
    private final DistrictRepository districtRepository;
    private final RecipeRepository recipeRepository;
    private final ContractRepository contractRepository;
    private MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;
    private ContractMedicineAmountRepository contractMedicineAmountRepository;
    private UserRepository userRepository;

    public RecordDTO getFilteredRecords(Long regionId, Long districtId, Long workplaceId, Field field, UUID userId, Long medicineId, LocalDate startDate, LocalDate endDate) {
//        if (medicineId != null) {
//            return filterByMedicineId(medicineId, startDate, endDate);
//        } else
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setTopProductsOnSellDTO(medicineWithQuantityDoctorRepository.findTop6MostSoldMedicinesWithFilters(districtId,regionId,workplaceId));
        recordDTO.setActiveDoctorSalesData(recipeRepository.getMonthlySales());
        recordDTO.setDashboardDoctorsCoverage(userRepository.getDoctorsCoverage());
        if (userId != null) {
            return filterByQuery(recordDTO,userId, startDate, endDate);
        } else if (field != null) {
            return filterByField(recordDTO,workplaceId, field, startDate, endDate);
        } else if (workplaceId != null) {
            return filterByWorkplaceId(recordDTO,workplaceId, startDate, endDate);
        } else if (districtId != null) {
            return filterByDistrictId(recordDTO,districtId, startDate, endDate);
        } else if (regionId != null) {
            return filterByRegionId(recordDTO,regionId, startDate, endDate);
        }


        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotes());
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmount());
        List<StatsEmployeeDTO> userCountByRegion = userRepository.getUserCountByRegion();
        RecordRegionDTO recordRegionDTO = new RecordRegionDTO();
        recordRegionDTO.setEmployeeStatsList(userCountByRegion);

        List<RecordStatsEmployeeFactDTO> recordStatsEmployeeFactDTOS = fillRegion();
        recordRegionDTO.setRecordStatsEmployeeFactList(recordStatsEmployeeFactDTOS);
        List<RegionFieldDTO> results = userRepository.countUsersByFieldAndRegion();

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS=new ArrayList<>();
        for (RegionFieldDTO row : results) {
            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField( row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(row.getAmount());
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        recordRegionDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordRegionDTO(recordRegionDTO);

        return recordDTO;
    }

    private RecordDTO filterByMedicineId(Long medicineId, LocalDate startDate, LocalDate endDate) {
        // Implement logic for filtering by medicineId
        return new RecordDTO();
    }

    private RecordDTO filterByQuery(RecordDTO dto, UUID userId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByUserId(userId));
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByUserId(userId));
        return recordDTO;
    }

    private RecordDTO filterByField(RecordDTO dto, Long workPlaceId, Field field, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByWorkplaceAndField(workPlaceId, field.name()));
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByWorkplaceAndField(workPlaceId, field.name()));
        return recordDTO;
    }

    private RecordDTO filterByWorkplaceId(RecordDTO dto, Long workplaceId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByDistrictAndWorkplace(workplaceId));
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByDistrictAndWorkplace(workplaceId));
        List<RegionFieldDTO> results = userRepository.countUsersByFieldAndWorkplace(workplaceId);

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS=new ArrayList<>();
        for (RegionFieldDTO row : results) {
            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField(row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(row.getAmount());
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        RecordWorkPlaceDTO recordWorkPlaceDTO= new RecordWorkPlaceDTO();
        recordWorkPlaceDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordWorkPlaceDTO(recordWorkPlaceDTO);
        return recordDTO;
    }

    private RecordDTO filterByDistrictId(RecordDTO dto, Long districtId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByDistrict(districtId));
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByDistrictId(districtId));
        List<RegionFieldDTO>  results = userRepository.countUsersByFieldAndDistrict(districtId);

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS=new ArrayList<>();
        for (RegionFieldDTO row : results) {
            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField(row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(row.getAmount());
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        RecordDistrictDTO recordDistrictDTO= new RecordDistrictDTO();
        recordDistrictDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordDistrictDTO(recordDistrictDTO);
        return recordDTO;
    }

    private RecordDTO filterByRegionId(RecordDTO dto, Long regionId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        recordDTO.setQuote(medicineWithQuantityDoctorRepository.getTotalQuotesByRegion(regionId));
        recordDTO.setSales(contractMedicineAmountRepository.getTotalContractMedicineDoctorAmountByRegion(regionId));
        List<StatsEmployeeDTO> userCountByRegion = userRepository.getUserCountByDistrictInRegion(regionId);
        RecordDistrictDTO recordDistrictDTO = new RecordDistrictDTO();
        recordDistrictDTO.setEmployeeStatsList(userCountByRegion);

        List<RecordStatsEmployeeFactDTO> recordStatsEmployeeFactDTOS = fillDistrict(regionId);
        recordDistrictDTO.setRecordStatsEmployeeFactList(recordStatsEmployeeFactDTOS);
        List<RegionFieldDTO> results = userRepository.countUsersByFieldAndRegion(regionId);

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS=new ArrayList<>();
        for (RegionFieldDTO row : results) {
            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField(row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(row.getAmount());
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        recordDistrictDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordDistrictDTO(recordDistrictDTO);
        return recordDTO;
    }

    private List<RecordStatsEmployeeFactDTO> fillRegion() {
        List<RecordStatsEmployeeFactDTO> list=new ArrayList<>();
            List<Region> regions = regionRepository.findAll();
            for (Region region : regions) {
                RecordStatsEmployeeFactDTO recordStatsEmployeeFactDTO = new RecordStatsEmployeeFactDTO();
                recordStatsEmployeeFactDTO.setId(String.valueOf(region.getId()));
                recordStatsEmployeeFactDTO.setName(region.getName());
                recordStatsEmployeeFactDTO.setNameRussian(region.getNameRussian());
                recordStatsEmployeeFactDTO.setNameUzCyrillic(region.getNameUzCyrillic());
                recordStatsEmployeeFactDTO.setNameUzLatin(region.getNameUzLatin());
                recordStatsEmployeeFactDTO.setLpuAmount(workPlaceRepository.countByRegionId(region.getId()));
                recordStatsEmployeeFactDTO.setDoctorsByDB(userRepository.countByRegionId(region.getId()));
                recordStatsEmployeeFactDTO.setDoctorsInFact(userRepository.countByRegionIdInFact(region.getId()));
                recordStatsEmployeeFactDTO.setPopulation(1000);
                list.add(recordStatsEmployeeFactDTO);
            }


        return list;
    }
    private List<RecordStatsEmployeeFactDTO> fillDistrict(Long regionId) {
        List<RecordStatsEmployeeFactDTO> list=new ArrayList<>();
        List<District> districts = districtRepository.findByRegionId(regionId);
        for (District district : districts) {
            RecordStatsEmployeeFactDTO recordStatsEmployeeFactDTO = new RecordStatsEmployeeFactDTO();
            recordStatsEmployeeFactDTO.setId(String.valueOf(district.getId()));
            recordStatsEmployeeFactDTO.setName(district.getName());
            recordStatsEmployeeFactDTO.setNameRussian(district.getNameRussian());
            recordStatsEmployeeFactDTO.setNameUzCyrillic(district.getNameUzCyrillic());
            recordStatsEmployeeFactDTO.setNameUzLatin(district.getNameUzLatin());
            recordStatsEmployeeFactDTO.setLpuAmount(workPlaceRepository.countByDistrictId(district.getId()));
            recordStatsEmployeeFactDTO.setDoctorsByDB(userRepository.countByDistrictId(district.getId()));
            recordStatsEmployeeFactDTO.setDoctorsInFact(userRepository.countByDistrictIdInFact(district.getId()));
            recordStatsEmployeeFactDTO.setPopulation(1000);
            list.add(recordStatsEmployeeFactDTO);
        }

        return list;
    }

}
