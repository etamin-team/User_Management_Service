package com.example.user_management_service.service;

import com.example.user_management_service.model.District;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.model.dto.UserDTO;
import com.example.user_management_service.model.dto.WorkPlaceDTO;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.WorkPlaceRepository;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private final WorkPlaceRepository workPlaceRepository;

    public Page<UserDTO> getDoctorsNotDeclinedAndNotEnabled(Pageable pageable) {
        return userRepository.findDoctorsByStatus(Role.DOCTOR, UserStatus.PENDING, pageable)
                .map(this::convertToDTO);
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
                user.getStatus(),
                user.getCreatorId(),
                user.getWorkplace()==null ? null : user.getWorkplace().getId(),
                user.getDistrict().getId()
        );
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

    public void createWorkPlace(WorkPlaceDTO workPlaceDTO) {
        WorkPlace workPlace = convertToEntity(workPlaceDTO);
        workPlaceRepository.save(workPlace);
    }

    private WorkPlace convertToEntity(WorkPlaceDTO workPlaceDTO) {
        WorkPlace workPlace = new WorkPlace();
        workPlace.setName(workPlaceDTO.getName());
        workPlace.setAddress(workPlaceDTO.getAddress());
        workPlace.setDescription(workPlaceDTO.getDescription());
        District district = new District();
        district.setId(workPlaceDTO.getDistrictId());
        workPlace.setDistrict(district);
        return workPlace;
    }


    public void updateWorkPlace(Long id, WorkPlaceDTO workPlaceDTO) {
        WorkPlace existingWorkPlace = workPlaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkPlace not found with id: " + id));

        existingWorkPlace.setName(workPlaceDTO.getName());
        existingWorkPlace.setAddress(workPlaceDTO.getAddress());
        existingWorkPlace.setDescription(workPlaceDTO.getDescription());

        if (workPlaceDTO.getDistrictId() != null) {
            District District = new District();
            District.setId(workPlaceDTO.getDistrictId());
            existingWorkPlace.setDistrict(District);
        }

        workPlaceRepository.save(existingWorkPlace);
    }

    public void deleteWorkPlace(Long id) {
        workPlaceRepository.deleteById(id);
    }


}
