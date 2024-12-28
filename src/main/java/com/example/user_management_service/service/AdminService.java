package com.example.user_management_service.service;

import com.example.user_management_service.model.User;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Date-12/20/2024
 * By Sardor Tokhirov
 * Time-3:37 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public Page<User> getDoctorsNotDeclinedAndNotEnabled(Pageable pageable) {
        return userRepository.findDoctorsByStatus(Role.DOCTOR,UserStatus.PENDING, pageable);
    }

    public void enableUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.ENABLED);
        userRepository.save(user);
    }

    public void declineUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.DECLINED);
        userRepository.save(user);
    }

    public void createWorkPlace(WorkPlace workPlace) {

    }
}
