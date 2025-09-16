package com.example.user_management_service.service;

import com.example.user_management_service.exception.DataNotFoundException;
import com.example.user_management_service.exception.NotFoundException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.repository.v2.DoctorContractV2Repository;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-2:59 AM (GMT+5)
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final WorkPlaceRepository workPlaceRepository;
    private final DistrictRepository districtRepository;
    private final DistrictRegionService districtRegionService;
    private final FieldForceRegionsRepository fieldForceRegionsRepository;
    private final RegionRepository regionRepository;
    private final MedAgentGroupRepository medAgentGroupRepository;
    private final DoctorContractV2Repository doctorContractV2Repository;

    public UserDTO getUserById(UUID userId) {
        return convertToDTO(userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found by ID " + userId)));
    }


    public boolean changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(UUID.fromString(changePasswordRequest.getUserId())).orElseThrow(() -> new DataNotFoundException("User not found by ID " + changePasswordRequest.getUserId()));

        if (user != null) {

            if (passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {

                if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
                    user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                    userRepository.save(user);
                    return true;
                }
            }
        }
        return false;
    }

    public UserDTO convertToDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getDateOfBirth(),
                user.getCreatedDate(),
                user.getPhoneNumber(),
                user.getNumber(),
                user.getEmail(),
                user.getPosition(),
                user.getFieldName(),
                user.getGender(),
                user.getStatus(),
                user.getCreatorId(),
                user.getWorkplace() == null ? null : user.getWorkplace().getId(),
                user.getDistrict() == null ? null : user.getDistrict().getId(),
                user.getRole(),
                user.getDistrict() == null ? null : districtRegionService.regionDistrictDTO(user.getDistrict()),
                user.getWorkplace() == null ? null : convertToDTO(user.getWorkplace()),
                false,
                (user.getRole()!=null?(user.getRole().getRoleName().equals(Role.MEDAGENT)?medAgentGroupRepository.findByUserId(user.getUserId()).orElseThrow(()->new RuntimeException("Group Name and SOme shit")).getGroupName():null):null)
        );
    }

    public UserDTO convertToDTOWithContract(User user) {
        boolean isContractPresent = doctorContractV2Repository.findActiveOrPendingContractByDoctorId(user.getUserId()).isEmpty();
        if (user == null) return null;
        return new UserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getDateOfBirth(),
                user.getCreatedDate(),
                user.getPhoneNumber(),
                user.getNumber(),
                user.getEmail(),
                user.getPosition(),
                user.getFieldName(),
                user.getGender(),
                user.getStatus(),
                user.getCreatorId(),
                user.getWorkplace() == null ? null : user.getWorkplace().getId(),
                user.getDistrict() == null ? null : user.getDistrict().getId(),
                user.getRole(),
                districtRegionService.regionDistrictDTO(user.getDistrict()),
                convertToDTO(user.getWorkplace()),
                !isContractPresent,
                (user.getRole()!=null?(user.getRole().getRoleName().equals(Role.MEDAGENT)?medAgentGroupRepository.findByUserId(user.getUserId()).orElseThrow(()->new RuntimeException("Group Name and SOme shit")).getGroupName():null):null)
        );
    }

    private WorkPlaceDTO convertToDTO(WorkPlace workPlace) {
        if (workPlace == null) return null;
        return new WorkPlaceDTO(
                workPlace.getId(),
                workPlace.getName(),
                workPlace.getAddress(),
                workPlace.getDescription(),
                workPlace.getPhone(),
                workPlace.getEmail(),
                workPlace.getMedicalInstitutionType(), // Include MedicalInstitutionType here
                workPlace.getChiefDoctor() != null ? workPlace.getChiefDoctor().getUserId() : null,
                workPlace.getDistrict() != null ? workPlace.getDistrict().getId() : null
        );


    }

    public List<UserDTO> getDoctors(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery, Field field, boolean withContracts) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        if (withContracts) {
            List<User> users = userRepository.findUsersByFiltersWitContracts(Role.DOCTOR, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, field);

            return users.stream()
                    .map(this::convertToDTOWithContract)
                    .toList();
        }
        List<User> users = userRepository.findUsersByFilters(Role.DOCTOR, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, field);

        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<UserDTO> getDoctors(UUID creatorId, Long regionId, List<Long> regionIds, Long districtId, Long workplaceId, String nameQuery, Field field, boolean withContracts) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        if (withContracts) {
            List<User> users = userRepository.findUsersByFiltersWitContracts(regionIds,Role.DOCTOR, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, field);

            return users.stream()
                    .map(this::convertToDTOWithContract)
                    .toList();
        }
        List<User> users = userRepository.findUsersByFilters(regionIds,Role.DOCTOR, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, field);

        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Page<UserDTO> getDoctorsPage(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery, Field field, Long medicineId, boolean withContracts, LocalDate startDate, LocalDate endDate, int page, int size) {
        String[] filteredParts = prepareNameParts(nameQuery);


        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        if (withContracts) {
            return userRepository.findUsersByFiltersPaginatedWithContracts(
                    Role.DOCTOR,
                    creatorId != null ? String.valueOf(creatorId) : null,
                    regionId,
                    districtId,
                    workplaceId,
                    name1,
                    name2,
                    name3,
                    field,
                    medicineId,
                    pageable
            ).map(this::convertToDTO);
        }
        return userRepository.findUsersByFiltersPaginated(
                Role.DOCTOR,
                creatorId != null ? String.valueOf(creatorId) : null,
                regionId,
                districtId,
                workplaceId,
                name1,
                name2,
                name3,
                field,
                pageable
        ).map(this::convertToDTO);

    }
    public Page<UserDTO> getDoctorsPage(List<Long> regionIds,UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery, Field field, Long medicineId, boolean withContracts, LocalDate startDate, LocalDate endDate, int page, int size) {
        String[] filteredParts = prepareNameParts(nameQuery);


        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        if (withContracts) {
            return userRepository.findUsersByFiltersPaginatedWithContracts(
                    regionIds,
                    Role.DOCTOR,
                    creatorId != null ? String.valueOf(creatorId) : null,
                    regionId,
                    districtId,
                    workplaceId,
                    name1,
                    name2,
                    name3,
                    field,
                    medicineId,
                    pageable
            ).map(this::convertToDTO);
        }
        return userRepository.findUsersByFiltersPaginated(
                regionIds,
                Role.DOCTOR,
                creatorId != null ? String.valueOf(creatorId) : null,
                regionId,
                districtId,
                workplaceId,
                name1,
                name2,
                name3,
                field,
                pageable
        ).map(this::convertToDTO);

    }


    public List<UserDTO> getManagers(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(Role.MANAGER, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    public List<UserDTO> getManagers(List<Long> regionsIds, UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(regionsIds,Role.MANAGER, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<UserDTO> getSuperAdmins(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(Role.SUPERADMIN, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null)
                .stream()
                .map(this::convertToDTO)
                .toList();

    }

    public List<UserDTO> getAdmins(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(Role.ADMIN, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<UserDTO> getMedAgents(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(Role.MEDAGENT, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<UserDTO> getMedAgents(List<Long> regionIds,UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(regionIds,Role.MEDAGENT, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private String[] prepareNameParts(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return new String[0]; // Return empty array if no name query is provided
        }

        String[] nameParts = nameQuery.split(" ");
        List<String> cleanParts = new ArrayList<>();
        for (String part : nameParts) {
            if (part != null && !part.trim().isEmpty()) {
                cleanParts.add(part.trim());
            }
        }

        return cleanParts.toArray(new String[0]);
    }


    public UserDTO updateUser(String userId, UserDTO updatedUser) {
        User user = userRepository.findById(UUID.fromString(userId))
                .map(existingUser -> {
                    // Update the fields of the existing user from the UserDTO
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setStatus(updatedUser.getStatus());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setMiddleName(updatedUser.getMiddleName());
                    existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    // Assuming other fields like position, fieldName, etc., can be set similarly
                    existingUser.setPosition(updatedUser.getPosition());
                    existingUser.setFieldName(updatedUser.getFieldName());
                    existingUser.setGender(updatedUser.getGender());

                    // Update the District and WorkPlace if the IDs are provided in the DTO
                    if (updatedUser.getDistrictId() != null) {
                        existingUser.setDistrict(districtRepository.findById(updatedUser.getDistrictId()).orElse(null));
                    }
                    if (updatedUser.getWorkplaceId() != null) {
                        existingUser.setWorkplace(workPlaceRepository.findById(updatedUser.getWorkplaceId()).orElse(null));
                    }

                    // Set the last update timestamp
                    existingUser.setLastUpdateDate(LocalDateTime.now());

                    // Save and return the updated user
                    return userRepository.save(existingUser);
                })
                .orElse(null);
        if (user.getRole().equals(Role.MEDAGENT)) {
            MedAgentGroup byUserId = medAgentGroupRepository.findByUserId(user.getUserId()).orElse(null);
            if (byUserId == null) {
                byUserId=new MedAgentGroup();
                byUserId.setUser(user);
            }
            byUserId.setGroupName(updatedUser.getGroupName());
            medAgentGroupRepository.save(byUserId);
        }
        return convertToDTO(user);
    }


    @Transactional
    public boolean deleteUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User existingUser = user.get();

            // Remove references to avoid constraint violations
            existingUser.setDistrict(null);
            existingUser.setWorkplace(null);
            existingUser.setStatus(UserStatus.DISABLED);
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }

    public boolean comparePassword(UUID userId, String password) {
        User user = userRepository.findById(userId).orElseThrow();
        return passwordEncoder.matches(password, user.getPassword());
    }


    public DoctorsInfoDTO getDoctorsInfo(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        DoctorsInfoDTO doctorsInfoDTO = new DoctorsInfoDTO();
        System.out.println("---------------------------------------------------------------------------------------------1111111111111");
        doctorsInfoDTO.setAllDoctors(getAllDoctorsCount(creatorId, regionId, districtId, workplaceId, nameQuery));
        System.out.println("--------------------------------------------------------------------------22222222222222222222222222");
        doctorsInfoDTO.setDoctorsInFact(getDoctorsWithApprovedContractsCount(creatorId, regionId, districtId, workplaceId, nameQuery, null));
        System.out.println("----------------------33333333333333333333333333333333333333333333333333333");
        doctorsInfoDTO.setNewDoctors(getNewDoctorsCountThisMonth(creatorId, regionId, districtId, workplaceId, nameQuery));
        System.out.println("----------------------565454343434343");

        return doctorsInfoDTO;
    }

    public Long getAllDoctorsCount(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.countDoctorsByFilters(Role.DOCTOR,
                creatorId != null ? String.valueOf(creatorId) : null,
                regionId, districtId, workplaceId,
                name1, name2, name3);
    }

    public Long getDoctorsWithApprovedContractsCount(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery, Field fieldName) {
        String[] filteredParts = prepareNameParts(nameQuery);

        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return doctorContractV2Repository.countDoctorsWithApprovedContracts(
                String.valueOf(creatorId),
                regionId, districtId, workplaceId,
                name1, name2, name3, fieldName
        );
    }

    public Long getNewDoctorsCountThisMonth(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] nameParts = prepareNameParts(nameQuery);

        return userRepository.countUsersCreatedThisMonth(
                Role.DOCTOR,
                creatorId != null ? creatorId.toString() : null,
                regionId,
                districtId,
                workplaceId,
                nameParts.length > 0 ? nameParts[0] : "",
                nameParts.length > 1 ? nameParts[1] : "",
                nameParts.length > 2 ? nameParts[2] : ""
        );
    }

    public void createOrUpdateFieldForceRegions(FieldForceRegionsDTO fieldForceRegions) {
        if (fieldForceRegions == null) {
            return;
        }
        FieldForceRegions newFieldForceRegions = fieldForceRegionsRepository.findByUserId(fieldForceRegions.getFieldForceId()).orElse(new FieldForceRegions());
        newFieldForceRegions.setUser(userRepository.findById(fieldForceRegions.getFieldForceId()).orElseThrow(() -> new UsernameNotFoundException("User not found")));
        newFieldForceRegions.setRegionIds(fieldForceRegions.getForceRegionIds());
        fieldForceRegionsRepository.save(newFieldForceRegions);
    }

    public FieldForceRegionsInfoDTO getFieldForceRegionsByUserId(UUID userId) {
        FieldForceRegions fieldForceRegions = fieldForceRegionsRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("FieldForceRegions not found"));
        if (fieldForceRegions == null) {
            return null; // Return null if no data is found
        }

        return new FieldForceRegionsInfoDTO(
                fieldForceRegions.getId(),
                convertToDTO(fieldForceRegions.getUser()),
                fieldForceRegions.getRegionIds()
        );
    }

    public List<FieldForceRegionsInfoDTO> getFieldForceRegions() {
        List<FieldForceRegions> fieldForceRegionsList = fieldForceRegionsRepository.findAll();

        return fieldForceRegionsList.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private FieldForceRegionsInfoDTO convertToDTO(FieldForceRegions fieldForceRegions) {
        return new FieldForceRegionsInfoDTO(
                fieldForceRegions.getId(),
                convertToDTO(fieldForceRegions.getUser()),
                fieldForceRegions.getRegionIds()
        );
    }


    public List<RegisterRequest> parseFileDoctors(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(
                file.getInputStream(),
                new TypeReference<List<RegisterRequest>>() {
                }
        );
    }

    public UserDTO getUserByNumber(String number) {
        return convertToDTO(userRepository.findByNumber(number).orElseThrow(() -> new DataNotFoundException("User not found by number " + number)));

    }

    public Page<UserDTO> getMedAgentsPage( UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery, int page, int size) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        return userRepository.findUsersByFilters(Role.MEDAGENT, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null,pageable).map(this::convertToDTO);
    }

    public Page<UserDTO> getMedAgentsFieldForcePage(List<Long> regionIds, UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery, int page, int size) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        return userRepository.findUsersByFilters(regionIds,Role.MEDAGENT, creatorId != null ? String.valueOf(creatorId) : null, regionId, districtId, workplaceId, name1, name2, name3, null,pageable).map(this::convertToDTO);
    }
}
