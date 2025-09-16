package com.example.user_management_service.service;

import com.example.user_management_service.exception.*;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date-12/20/2024
 * By Sardor Tokhirov
 * Time-3:37 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final UserService userService;

    public Page<UserDTO> getDoctorsNotDeclinedAndNotEnabled(Pageable pageable) {
        return userRepository.findDoctorsByStatus(Role.DOCTOR, UserStatus.PENDING, pageable)
                .map((data)->userService.convertToDTO(data));
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





}
