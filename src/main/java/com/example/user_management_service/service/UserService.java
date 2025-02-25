package com.example.user_management_service.service;

import com.example.user_management_service.exception.DataNotFoundException;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.ChangePasswordRequest;
import com.example.user_management_service.model.dto.UserDTO;
import com.example.user_management_service.repository.DistrictRepository;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.WorkPlaceRepository;
import com.example.user_management_service.role.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserDTO getUserById(UUID userId) {
        return convertToDTO(userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found by ID " + userId)));
    }


    public boolean changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(UUID.fromString(changePasswordRequest.getUserId())).orElseThrow(()-> new DataNotFoundException("User not found by ID " + changePasswordRequest.getUserId()));

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
                user.getStatus(),
                user.getCreatorId(),
                user.getWorkplace()==null ? null : user.getWorkplace().getId(),
                user.getDistrict()==null ? null : user.getDistrict().getId(),
                user.getRole()
        );
    }


    public List<UserDTO> getDoctors(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(Role.DOCTOR, creatorId!=null?String.valueOf(creatorId):null, regionId, districtId, workplaceId, name1, name2, name3)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<UserDTO> getManagers(UUID creatorId, Long regionId, Long districtId, Long workplaceId, String nameQuery) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Get name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        return userRepository.findUsersByFilters(Role.MANAGER, creatorId!=null?String.valueOf(creatorId):null, regionId, districtId, workplaceId, name1, name2, name3)
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

        return userRepository.findUsersByFilters(Role.SUPERADMIN, creatorId!=null?String.valueOf(creatorId):null, regionId, districtId, workplaceId, name1, name2, name3)
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

        return userRepository.findUsersByFilters(Role.ADMIN, creatorId!=null?String.valueOf(creatorId):null, regionId, districtId, workplaceId, name1, name2, name3)
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

        return userRepository.findUsersByFilters(Role.MEDAGENT, creatorId!=null?String.valueOf(creatorId):null, regionId, districtId, workplaceId, name1, name2, name3)
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
        User user= userRepository.findById(UUID.fromString(userId))
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
            userRepository.save(existingUser);

            // Now delete the user
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public boolean comparePassword(UUID userId, String password) {
        User user = userRepository.findById(userId).orElseThrow();
        return passwordEncoder.matches(password, user.getPassword());
    }

}
