package com.example.user_management_service.service;

import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.RegionRepository;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public List<Region> getRegions(){
        return regionRepository.findAll();
    }

    public District getDistrict(Long DistrictId){
        return DistrictRepository.findById(DistrictId).get();
    }

    public Region getRegionByDistrict(Long DistrictId){
        return DistrictRepository.findById(DistrictId).get().getRegion();
    }
}
