package com.example.user_management_service.service;

import com.example.user_management_service.model.Country;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.repository.CountryRepository;
import com.example.user_management_service.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryRegionService {

    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;

    public CountryRegionService(RegionRepository regionRepository, CountryRepository countryRepository) {
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
    }

    public List<Country> getCountriesByRegion(Long regionId) {
        return countryRepository.findByRegionId(regionId);
    }

    public Region getRegionByName(String name) {
        return regionRepository.findByName(name);
    }
    public Country getCountryByName(String name) {
        return countryRepository.findByName(name);
    }
}
