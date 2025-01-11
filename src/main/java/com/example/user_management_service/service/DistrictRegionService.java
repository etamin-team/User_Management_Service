package com.example.user_management_service.service;

import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.dto.RegionDTO;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.RegionRepository;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistrictRegionService {

    private final RegionRepository regionRepository;
    private final DistrictRepository DistrictRepository;

    public DistrictRegionService(RegionRepository regionRepository, DistrictRepository DistrictRepository) {
        this.regionRepository = regionRepository;
        this.DistrictRepository = DistrictRepository;
    }

    public List<District> getDistrictsByRegionId(Long regionId) {
        return DistrictRepository.findByRegionId(regionId);
    }



    public List<RegionDTO> getRegions() {
        List<Region> regions = regionRepository.findAll();

        // Map the regions to RegionDTO
        return regions.stream().map(region -> new RegionDTO(
                region.getId(),
                region.getName(),
                region.getDistricts().stream().map(District::getName).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

    public District getDistrict(Long DistrictId){
        return DistrictRepository.findById(DistrictId).get();
    }

    public Region getRegionByDistrict(Long DistrictId){
        return DistrictRepository.findById(DistrictId).get().getRegion();
    }
}
