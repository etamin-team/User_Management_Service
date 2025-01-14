package com.example.user_management_service.service;

import com.example.user_management_service.model.User;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.model.dto.ChangePasswordRequest;
import com.example.user_management_service.model.dto.UserDTO;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.WorkPlaceRepository;
import com.example.user_management_service.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public User getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId)).orElse(null);
    }
    public boolean changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(UUID.fromString(changePasswordRequest.getUserId())).orElse(null);

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
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getDateOfBirth(),
                user.getPhoneNumber(),
                user.getNumber(),
                user.getEmail(),
                user.getPosition(),
                user.getFieldName(),
                user.getGender(),
                user.getWorkplace()==null ? null : user.getWorkplace().getId(),
                user.getDistrict().getId()
        );
    }


    public List<UserDTO> getDoctors(String creatorId, Long countryId, Long regionId, Long workplaceId, String nameQuery) {
        if (nameQuery != null && !nameQuery.isEmpty()) {
            return userRepository.searchDoctorsByName(Role.DOCTOR, nameQuery).stream().map(this::convertToDTO).toList();
//        } else if (creatorId != null && countryId != null && regionId != null && workplaceId != null) {
//            return userRepository.findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
//                    Role.DOCTOR, creatorId, countryId, regionId, workplaceId);
        } else if (creatorId != null) {
            return userRepository.findByCreatorId(creatorId).stream().map(this::convertToDTO).toList();
        } else {
            return userRepository.findDoctors().stream().map(this::convertToDTO).toList();
        }
    }

    public List<UserDTO> getManagers(String creatorId, Long countryId, Long regionId, Long workplaceId, String nameQuery) {
        if (nameQuery != null && !nameQuery.isEmpty()) {
            return userRepository.searchManagersByName(Role.MANAGER, nameQuery).stream().map(this::convertToDTO).toList();
//        } else if (creatorId != null && countryId != null && regionId != null && workplaceId != null) {
//            return userRepository.findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
//                    Role.MANAGER, creatorId, countryId, regionId, workplaceId);
        } else if (creatorId != null) {
            return userRepository.findByCreatorId(creatorId).stream().map(this::convertToDTO).toList();
        } else {
            return userRepository.findManagers().stream().map(this::convertToDTO).toList();
        }
    }

    public List<UserDTO> getSuperAdmins(String creatorId, Long countryId, Long regionId, Long workplaceId, String nameQuery) {
        if (nameQuery != null && !nameQuery.isEmpty()) {
            return userRepository.searchManagersByName(Role.SUPERADMIN, nameQuery).stream().map(this::convertToDTO).toList();
//        } else if (creatorId != null && countryId != null && regionId != null && workplaceId != null) {
//            return userRepository.findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
//                    Role.MANAGER, creatorId, countryId, regionId, workplaceId);
        } else if (creatorId != null) {
            return userRepository.findByCreatorId(creatorId).stream().map(this::convertToDTO).toList();
        } else {
            return userRepository.findManagers().stream().map(this::convertToDTO).toList();
        }
    }

    public List<UserDTO> getAdmins(String creatorId, Long countryId, Long regionId, Long workplaceId, String nameQuery) {
        if (nameQuery != null && !nameQuery.isEmpty()) {
            return userRepository.searchManagersByName(Role.ADMIN, nameQuery).stream().map(this::convertToDTO).toList();
//        } else if (creatorId != null && countryId != null && regionId != null && workplaceId != null) {
//            return userRepository.findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
//                    Role.MANAGER, creatorId, countryId, regionId, workplaceId);
        } else if (creatorId != null) {
            return userRepository.findByCreatorId(creatorId).stream().map(this::convertToDTO).toList();
        } else {
            return userRepository.findManagers().stream().map(this::convertToDTO).toList();
        }
    }
    public List<UserDTO> getMedAgents(String creatorId, Long countryId, Long regionId, Long workplaceId, String nameQuery) {
        if (nameQuery != null && !nameQuery.isEmpty()) {
            return userRepository.searchManagersByName(Role.MEDAGENT, nameQuery).stream().map(this::convertToDTO).toList();
//        } else if (creatorId != null && countryId != null && regionId != null && workplaceId != null) {
//            return userRepository.findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
//                    Role.MANAGER, creatorId, countryId, regionId, workplaceId);
        } else if (creatorId != null) {
            return userRepository.findByCreatorId(creatorId).stream().map(this::convertToDTO).toList();
        } else {
            return userRepository.findManagers().stream().map(this::convertToDTO).toList();
        }
    }
    public User updateUser(String userId, UserDTO updatedUser) {
        return userRepository.findById(UUID.fromString(userId))
                .map(existingUser -> {
                    // Update the fields of the existing user from the UserDTO
                    existingUser.setFirstName(updatedUser.getFirstName());
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
    }


    public boolean deleteUser(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (user.isPresent()) {
            userRepository.deleteById(UUID.fromString(userId));
            return true;
        }
        return false;
    }
//    public List<WorkPlace> getAllWorkplaces() {
//        return workPlaceRepository.findAll();
//    }

}
