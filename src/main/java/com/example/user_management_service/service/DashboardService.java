package com.example.user_management_service.service;

import com.example.user_management_service.exception.ChartException;
import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final UserService userService;
    private MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;
    private ContractMedicineDoctorAmountRepository contractMedicineDoctorAmountRepository;
    private UserRepository userRepository;
    private SalesRepository salesRepository;

    public RecordDTO getFilteredRecords(Long regionId, Long districtId, Long workplaceId, Field field, UUID userId, Long medicineId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = new RecordDTO();
        if (workplaceId != null) {
            return filterByWorkplaceId(recordDTO, workplaceId, startDate, endDate);
        } else if (districtId != null) {
            return filterByDistrictId(recordDTO, districtId, startDate, endDate);
        } else if (regionId != null) {
            return filterByRegionId(recordDTO, regionId, startDate, endDate);
        }

        List<StatsEmployeeDTO> userCountByRegion = userRepository.getUserCountByRegion();
        RecordRegionDTO recordRegionDTO = new RecordRegionDTO();
        recordRegionDTO.setEmployeeStatsList(userCountByRegion);

        List<RecordStatsEmployeeFactDTO> recordStatsEmployeeFactDTOS = fillRegion();
        recordRegionDTO.setRecordStatsEmployeeFactList(recordStatsEmployeeFactDTOS);
        List<RegionFieldDTO> results = userRepository.countUsersByFieldAndRegion();

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS = new ArrayList<>();
        for (RegionFieldDTO row : results) {
            Long inFact = userService.getDoctorsWithApprovedContractsCount(null, regionId, districtId, workplaceId, null, row.getField());
            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField(row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(inFact);
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        recordRegionDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordRegionDTO(recordRegionDTO);

        return recordDTO;
    }

    private RecordDTO filterByWorkplaceId(RecordDTO dto, Long workplaceId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        List<RegionFieldDTO> results = userRepository.countUsersByFieldAndWorkplace(workplaceId);

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS = new ArrayList<>();
        for (RegionFieldDTO row : results) {
            Long inFact = userService.getDoctorsWithApprovedContractsCount(null, null, null, workplaceId, null, row.getField());

            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField(row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(inFact);
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        RecordWorkPlaceDTO recordWorkPlaceDTO = new RecordWorkPlaceDTO();
        recordWorkPlaceDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordWorkPlaceDTO(recordWorkPlaceDTO);
        return recordDTO;
    }

    private RecordDTO filterByDistrictId(RecordDTO dto, Long districtId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        List<RegionFieldDTO> results = userRepository.countUsersByFieldAndDistrict(districtId);

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS = new ArrayList<>();
        for (RegionFieldDTO row : results) {
            Long inFact = userService.getDoctorsWithApprovedContractsCount(null, null, districtId, null, null, row.getField());
            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField(row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(inFact);
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        RecordDistrictDTO recordDistrictDTO = new RecordDistrictDTO();
        recordDistrictDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordDistrictDTO(recordDistrictDTO);
        return recordDTO;
    }

    private RecordDTO filterByRegionId(RecordDTO dto, Long regionId, LocalDate startDate, LocalDate endDate) {
        RecordDTO recordDTO = dto;
        List<StatsEmployeeDTO> userCountByRegion = userRepository.getUserCountByDistrictInRegion(regionId);
        RecordDistrictDTO recordDistrictDTO = new RecordDistrictDTO();
        recordDistrictDTO.setEmployeeStatsList(userCountByRegion);

        List<RecordStatsEmployeeFactDTO> recordStatsEmployeeFactDTOS = fillDistrict(regionId);
        recordDistrictDTO.setRecordStatsEmployeeFactList(recordStatsEmployeeFactDTOS);
        List<RegionFieldDTO> results = userRepository.countUsersByFieldAndRegion(regionId);

        List<RecordWorkPlaceStatsDTO> workPlaceStatsDTOS = new ArrayList<>();
        for (RegionFieldDTO row : results) {
            Long inFact = userService.getDoctorsWithApprovedContractsCount(null, regionId, null, null, null, row.getField());
            RecordWorkPlaceStatsDTO workPlaceStatsDTO = new RecordWorkPlaceStatsDTO();
            workPlaceStatsDTO.setField(row.getField());
            workPlaceStatsDTO.setDoctorsByDB(row.getAmount());
            workPlaceStatsDTO.setDoctorsInFact(inFact);
            workPlaceStatsDTOS.add(workPlaceStatsDTO);
        }
        recordDistrictDTO.setRecordWorkPlaceStatsDTOList(workPlaceStatsDTOS);
        recordDTO.setRecordDistrictDTO(recordDistrictDTO);
        return recordDTO;
    }

    private List<RecordStatsEmployeeFactDTO> fillRegion() {
        List<RecordStatsEmployeeFactDTO> list = new ArrayList<>();
        List<Region> regions = regionRepository.findAll();
        for (Region region : regions) {
            Long inFact = userService.getDoctorsWithApprovedContractsCount(null, region.getId(), null, null, null, null);
            RecordStatsEmployeeFactDTO recordStatsEmployeeFactDTO = new RecordStatsEmployeeFactDTO();
            recordStatsEmployeeFactDTO.setId(String.valueOf(region.getId()));
            recordStatsEmployeeFactDTO.setName(region.getName());
            recordStatsEmployeeFactDTO.setNameRussian(region.getNameRussian());
            recordStatsEmployeeFactDTO.setNameUzCyrillic(region.getNameUzCyrillic());
            recordStatsEmployeeFactDTO.setNameUzLatin(region.getNameUzLatin());
            recordStatsEmployeeFactDTO.setLpuAmount(workPlaceRepository.countByRegionId(region.getId()));
            recordStatsEmployeeFactDTO.setDoctorsByDB(userRepository.countByRegionId(region.getId()));
            recordStatsEmployeeFactDTO.setDoctorsInFact(inFact);
            recordStatsEmployeeFactDTO.setPopulation(0);
            list.add(recordStatsEmployeeFactDTO);
        }


        return list;
    }

    private List<RecordStatsEmployeeFactDTO> fillDistrict(Long regionId) {
        List<RecordStatsEmployeeFactDTO> list = new ArrayList<>();
        List<District> districts = districtRepository.findByRegionId(regionId);
        for (District district : districts) {
            Long inFact = userService.getDoctorsWithApprovedContractsCount(null, null, district.getId(), null, null, null);
            RecordStatsEmployeeFactDTO recordStatsEmployeeFactDTO = new RecordStatsEmployeeFactDTO();
            recordStatsEmployeeFactDTO.setId(String.valueOf(district.getId()));
            recordStatsEmployeeFactDTO.setName(district.getName());
            recordStatsEmployeeFactDTO.setNameRussian(district.getNameRussian());
            recordStatsEmployeeFactDTO.setNameUzCyrillic(district.getNameUzCyrillic());
            recordStatsEmployeeFactDTO.setNameUzLatin(district.getNameUzLatin());
            recordStatsEmployeeFactDTO.setLpuAmount(workPlaceRepository.countByDistrictId(district.getId()));
            recordStatsEmployeeFactDTO.setDoctorsByDB(userRepository.countByDistrictId(district.getId()));
            recordStatsEmployeeFactDTO.setDoctorsInFact(inFact);
            recordStatsEmployeeFactDTO.setPopulation(0);
            list.add(recordStatsEmployeeFactDTO);
        }

        return list;
    }


    public List<LineChart> getRecipeChartSales(LocalDate startDate, LocalDate endDate, int numberOfParts) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0 || numberOfParts <= 0) {
            throw new ChartException("Invalid date range or part count");
        }

        long interval = totalDays / numberOfParts;
        List<LineChart> chart = new ArrayList<>();

        for (int i = 0; i < numberOfParts; i++) {
            LocalDate from = startDate.plusDays(i * interval);
            LocalDate to = (i == numberOfParts - 1) ? endDate : from.plusDays(interval - 1);

            Long totalPrice = recipeRepository.getTotalPriceBetweenDatesAndDoctor(from, to);
            if (totalPrice == null) totalPrice = 0L;

            chart.add(new LineChart(from, to, totalPrice));
        }

        return chart;
    }

    public List<LineChart> getRecipeChartQuote(LocalDate startDate, LocalDate endDate, int numberOfParts) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0 || numberOfParts <= 0) {
            throw new ChartException("Invalid date range or part count");
        }

        long interval = totalDays / numberOfParts;
        List<LineChart> chart = new ArrayList<>();

        for (int i = 0; i < numberOfParts; i++) {
            LocalDate from = startDate.plusDays(i * interval);
            LocalDate to = (i == numberOfParts - 1) ? endDate : from.plusDays(interval - 1);

            Long totalPrice = contractRepository.getTotalContractQuotesBetweenDates(from, to);
            if (totalPrice == null) totalPrice = 0L;

            chart.add(new LineChart(from, to, totalPrice));
        }

        return chart;
    }

    public List<TopProductsOnSellDTO> getTop6Medicine(Long regionId, Long districtId, Long workplaceId, LocalDate startDate, LocalDate endDate) {
        return medicineWithQuantityDoctorRepository.findTop6MostSoldMedicinesWithFilters(districtId, regionId, workplaceId,startDate,endDate);
    }

    public SalesQuoteDTO getSalesQuote(Long regionId, LocalDate startDate, LocalDate endDate) {
        SalesQuoteDTO salesQuoteDTO=new SalesQuoteDTO();
        salesQuoteDTO.setQuote(salesRepository.getTotalQuotes(regionId,startDate,endDate));
        salesQuoteDTO.setSales(salesRepository.getTotalAmounts(regionId,startDate,endDate));
        return salesQuoteDTO;
    }

    public List<ContractTypeSalesData> getContractTypeSalesData() {
        return recipeRepository.getTotalSoldByContractType();
    }

    public List<ActiveDoctorSalesData> getActiveDoctorSalesData() {
        return recipeRepository.getMonthlySales();
    }

    public List<DashboardDoctorsCoverage> getDashboardDoctorsCoverages() {
        return contractRepository.getDoctorsCoverage();
    }
}
