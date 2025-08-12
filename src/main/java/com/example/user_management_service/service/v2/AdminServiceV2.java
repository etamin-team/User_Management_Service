package com.example.user_management_service.service.v2;

import com.example.user_management_service.exception.ReportException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Date-8/7/2025
 * By Sardor Tokhirov
 * Time-3:27 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceV2 {

    private final MedicineWithQuantityDoctorRepository medicineWithQuantityDoctorRepository;
    private final MedicineRepository medicineRepository;

    private final UserRepository userRepository;


    private final DistrictRepository districtRepository;

    public List<AdminPrescriptions> getAdminPrescriptions(String name, Long regionId, Long districtId, Long workPlaceId, Field field, LocalDate startDate, LocalDate endDate) {
        List<AdminPrescriptions> adminPrescriptions = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate(name);
        for (Medicine medicine : medicines) {
            Long recipe = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.RECIPE, startDate, endDate);

            Long su = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.SU, startDate, endDate);

            Long sb = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.SB, startDate, endDate);

            Long gz = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.GZ, startDate, endDate);

            Long kb = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.KZ, startDate, endDate);

            Long written = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field,null, startDate, endDate);

            Long quote = medicineWithQuantityDoctorRepository.findTotalQuotePrescriptions(medicine.getId(), regionId, districtId, workPlaceId, field, startDate, endDate);

            AdminPrescriptions adminPrescription = new AdminPrescriptions();
            adminPrescription.setQuote(quote);
            adminPrescription.setWritten(written);
            adminPrescription.setRecipe(recipe);
            adminPrescription.setSu(su);
            adminPrescription.setSb(sb);
            adminPrescription.setGz(gz);
            adminPrescription.setKb(kb);
            adminPrescription.setMedicine(medicine);
            adminPrescription.setMedicineId(medicine.getId());
            adminPrescriptions.add(adminPrescription);
        }

        return adminPrescriptions;
    }


    public Page<AdminPrescriptionsMedicine> getAdminPrescriptionsByMedicineId(
            Long medicineId,
            Long regionId,
            Long districtId,
            Long workPlaceId,
            Field field,
            ContractType contractType,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        Page<SimpleDoctorPrescriptionDTO> prescriptions = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByDoctor(
                medicineId, regionId, districtId, workPlaceId, field, contractType, startDate, endDate, pageable
        );

        return prescriptions.map(dto -> {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            District district = user != null ? districtRepository.findById(user.getDistrict().getId()).orElse(null) : null;

            UserDTO userDTO = user != null ? new UserDTO(
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
                    user.getWorkplace() != null ? user.getWorkplace().getId() : null,
                    user.getDistrict() != null ? user.getDistrict().getId() : null,
                    user.getRole(),
                    district != null ? new RegionDistrictDTO(
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
                    ) : null,
                    null, true, null
            ) : null;
            return new AdminPrescriptionsMedicine(
                    dto.getMedicine(),
                    userDTO,
                    dto.getWritten()
            );
        });
    }
}
