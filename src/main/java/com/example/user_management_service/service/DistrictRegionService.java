package com.example.user_management_service.service;

import com.example.user_management_service.exception.DataNotFoundException;
import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.dto.DistrictDTO;
import com.example.user_management_service.model.dto.RegionDTO;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.RegionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DistrictRegionService {

    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;


    // Method to map Region to RegionDTO
    private RegionDTO mapRegionToDTO(Region region) {
        List<DistrictDTO> districtDTOList = region.getDistricts().stream()
                .map(district -> new DistrictDTO(
                        district.getId(),
                        district.getName(),
                        district.getNameUzCyrillic(),
                        district.getNameUzLatin(),
                        district.getNameRussian(),
                        district.getRegion().getId()
                ))
                .collect(Collectors.toList());

        return new RegionDTO(
                region.getId(),
                region.getName(),            // Default region name (primary language)
                region.getNameUzCyrillic(),
                region.getNameUzLatin(),
                region.getNameRussian(),
                districtDTOList
        );
    }


    public List<RegionDTO> getRegions() {
        List<Region> regionList = regionRepository.findAll();
        return regionList.stream()
                .map(this::mapRegionToDTO)
                .collect(Collectors.toList());
    }

    public RegionDTO getRegionById(Long id) {
        return mapRegionToDTO(regionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Region with ID " + id + " not found")));
    }

    public DistrictDTO getDistrictById(Long id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("District with ID " + id + " not found"));

        return new DistrictDTO(
                district.getId(),
                district.getName(),
                district.getNameUzCyrillic(),
                district.getNameUzLatin(),
                district.getNameRussian(),
                district.getRegion().getId()
        );
    }

    public List<DistrictDTO> getDistrictsByRegionId(Long regionId) {
        List<District> districtList = districtRepository.findByRegionId(regionId);
        if (districtList.isEmpty()) {
            throw new DataNotFoundException("No districts found for region ID: " + regionId);
        }
        return districtList.stream()
                .map(district -> new DistrictDTO(
                        district.getId(),
                        district.getName(),
                        district.getNameUzCyrillic(),
                        district.getNameUzLatin(),
                        district.getNameRussian(),
                        district.getRegion().getId()
                ))
                .collect(Collectors.toList());
    }

    public District getDistrict(Long districtId) {
        return districtRepository.findById(districtId)
                .orElseThrow(() -> new DataNotFoundException("District with ID " + districtId + " not found"));
    }

    public Region getRegionByDistrict(Long districtId) {
        return districtRepository.findById(districtId).get().getRegion();
    }

}
