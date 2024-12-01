package com.example.user_management_service.service;

import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.ChangePasswordRequest;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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


    public List<User> getDoctors(String creatorId, Long countryId, Long regionId, Long workplaceId, String nameQuery) {
        if (nameQuery != null && !nameQuery.isEmpty()) {
            return userRepository.searchDoctorsByName(Role.DOCTOR, nameQuery);
        } else if (creatorId != null && countryId != null && regionId != null && workplaceId != null) {
            return userRepository.findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
                    Role.DOCTOR, creatorId, countryId, regionId, workplaceId);
        } else if (creatorId != null) {
            return userRepository.findByCreatorId(creatorId);
        } else {
            return userRepository.findDoctors();
        }
    }
    public List<User> getManagers(String creatorId, Long countryId, Long regionId, Long workplaceId, String nameQuery) {
        if (nameQuery != null && !nameQuery.isEmpty()) {
            return userRepository.searchManagersByName(Role.MANAGER, nameQuery);
        } else if (creatorId != null && countryId != null && regionId != null && workplaceId != null) {
            return userRepository.findByRoleAndCreatorIdAndCountryIdAndRegionIdAndWorkplaceId(
                    Role.MANAGER, creatorId, countryId, regionId, workplaceId);
        } else if (creatorId != null) {
            return userRepository.findByCreatorId(creatorId);
        } else {
            return userRepository.findManagers();
        }
    }
}
