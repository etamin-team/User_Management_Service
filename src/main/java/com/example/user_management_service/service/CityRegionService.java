package com.example.user_management_service.service;

import com.example.user_management_service.model.City;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.repository.CityRepository;
import com.example.user_management_service.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityRegionService {

    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    public CityRegionService(RegionRepository regionRepository, CityRepository cityRepository) {
        this.regionRepository = regionRepository;
        this.cityRepository = cityRepository;
    }

    public List<Region> getCitiesByRegions(Long regionId) {
        return regionRepository.findByRegionId(regionId);
    }

    public Region getRegionByName(String name) {
        return regionRepository.findByName(name);
    }
    public City getCityByName(String name) {
        return cityRepository.findByName(name);
    }
}
