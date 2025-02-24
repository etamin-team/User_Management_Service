package com.example.user_management_service.service;

import com.example.user_management_service.exception.DataNotFoundException;
import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.dto.DistrictDTO;
import com.example.user_management_service.model.dto.RegionDTO;
import com.example.user_management_service.model.dto.RegionDistrictDTO;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.RegionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DistrictRegionService {

    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    @Transactional
    public RegionDTO addRegion(RegionDTO regionDTO) {
        Region region = new Region();
        region.setName(regionDTO.getName());
        region.setNameUzCyrillic(regionDTO.getNameUzCyrillic());
        region.setNameUzLatin(regionDTO.getNameUzLatin());
        region.setNameRussian(regionDTO.getNameRussian());
        region = regionRepository.save(region);
        return new RegionDTO(region.getId(), region.getName(), region.getNameUzCyrillic(), region.getNameUzLatin(), region.getNameRussian(), null);
    }

    @Transactional
    public List<RegionDTO> addRegions(List<RegionDTO> regionDTOs) {
        List<Region> regions = regionDTOs.stream().map(dto -> {
            Region region = new Region();
            region.setName(dto.getName());
            region.setNameUzCyrillic(dto.getNameUzCyrillic());
            region.setNameUzLatin(dto.getNameUzLatin());
            region.setNameRussian(dto.getNameRussian());
            return region;
        }).collect(Collectors.toList());
        regions = regionRepository.saveAll(regions);
        return regions.stream().map(region -> new RegionDTO(region.getId(), region.getName(), region.getNameUzCyrillic(), region.getNameUzLatin(), region.getNameRussian(), null)).collect(Collectors.toList());
    }

    @Transactional
    public DistrictDTO addDistrict(DistrictDTO districtDTO) {
        Region region = regionRepository.findById(districtDTO.getRegionId())
                .orElseThrow(() -> new RuntimeException("Region not found"));
        District district = new District();
        district.setName(districtDTO.getName());
        district.setNameUzCyrillic(districtDTO.getNameUzCyrillic());
        district.setNameUzLatin(districtDTO.getNameUzLatin());
        district.setNameRussian(districtDTO.getNameRussian());
        district.setRegion(region);
        district = districtRepository.save(district);
        return new DistrictDTO(district.getId(), district.getName(), district.getNameUzCyrillic(), district.getNameUzLatin(), district.getNameRussian(), region.getId());
    }

    @Transactional
    public List<DistrictDTO> addDistricts(List<DistrictDTO> districtDTOs) {
        return districtDTOs.stream().map(this::addDistrict).collect(Collectors.toList());
    }

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

    public RegionDistrictDTO regionDistrictDTO(Long districtId){
        District district = districtRepository.findById(districtId).orElseThrow();
        return new RegionDistrictDTO(
                district.getRegion().getId(),
                district.getRegion().getName(),
                district.getRegion().getNameUzCyrillic(),
                district.getRegion().getNameUzLatin(),
                district.getRegion().getNameRussian(),
                district.getId(),
                district.getName(),
                district.getNameUzCyrillic(),
                district.getNameUzLatin(),
                district.getNameRussian()
        );
    }
    public RegionDistrictDTO regionDistrictDTO(District district){
        return new RegionDistrictDTO(
                district.getRegion().getId(),
                district.getRegion().getName(),
                district.getRegion().getNameUzCyrillic(),
                district.getRegion().getNameUzLatin(),
                district.getRegion().getNameRussian(),
                district.getId(),
                district.getName(),
                district.getNameUzCyrillic(),
                district.getNameUzLatin(),
                district.getNameRussian()
        );
    }

    @Transactional
    public RegionDTO updateRegion(Long id, RegionDTO regionDTO) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Region with ID " + id + " not found"));
        region.setName(regionDTO.getName());
        region.setNameUzCyrillic(regionDTO.getNameUzCyrillic());
        region.setNameUzLatin(regionDTO.getNameUzLatin());
        region.setNameRussian(regionDTO.getNameRussian());
        region = regionRepository.save(region);
        return new RegionDTO(region.getId(), region.getName(), region.getNameUzCyrillic(), region.getNameUzLatin(), region.getNameRussian(), null);
    }

    @Transactional
    public void deleteRegion(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new DataNotFoundException("Region with ID " + id + " not found");
        }
        regionRepository.deleteById(id);
    }

    @Transactional
    public DistrictDTO updateDistrict(Long id, DistrictDTO districtDTO) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("District with ID " + id + " not found"));
        Region region = regionRepository.findById(districtDTO.getRegionId())
                .orElseThrow(() -> new DataNotFoundException("Region with ID " + districtDTO.getRegionId() + " not found"));
        district.setName(districtDTO.getName());
        district.setNameUzCyrillic(districtDTO.getNameUzCyrillic());
        district.setNameUzLatin(districtDTO.getNameUzLatin());
        district.setNameRussian(districtDTO.getNameRussian());
        district.setRegion(region);
        district = districtRepository.save(district);
        return new DistrictDTO(district.getId(), district.getName(), district.getNameUzCyrillic(), district.getNameUzLatin(), district.getNameRussian(), region.getId());
    }

    @Transactional
    public void deleteDistrict(Long id) {
        if (!districtRepository.existsById(id)) {
            throw new DataNotFoundException("District with ID " + id + " not found");
        }
        districtRepository.deleteById(id);
    }
}
