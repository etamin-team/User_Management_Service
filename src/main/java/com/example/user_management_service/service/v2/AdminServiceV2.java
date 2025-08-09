package com.example.user_management_service.service.v2;

import com.example.user_management_service.exception.ReportException;
import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.AdminPrescriptions;
import com.example.user_management_service.model.dto.AdminPrescriptionsMedicine;
import com.example.user_management_service.repository.ContractRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.MedicineWithQuantityDoctorRepository;
import com.example.user_management_service.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final RecipeRepository recipeRepository;

    public List<AdminPrescriptions> getAdminPrescriptions(String name, Long regionId, Long districtId, Long workPlaceId, Field field, LocalDate startDate, LocalDate endDate) {
        List<AdminPrescriptions> adminPrescriptions = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAllSortByCreatedDate(name);
        for (Medicine medicine : medicines) {
            System.out.println("11111111111");
            Long recipe = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.RECIPE, startDate, endDate);
            System.out.println("111111111112");

            Long su = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.SU, startDate, endDate);
            System.out.println("11111111113");

            Long sb = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.SB, startDate, endDate);
            System.out.println("11111111114");

            Long gz = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.GZ, startDate, endDate);
            System.out.println("11111111115");

            Long kb = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field, ContractType.KZ, startDate, endDate);
            System.out.println("11111111116");

            Long written = medicineWithQuantityDoctorRepository.findTotalPrescriptionsByContractType(medicine.getId(), regionId, districtId, workPlaceId, field,null, startDate, endDate);
            System.out.println("11111111117");

            Long quote = medicineWithQuantityDoctorRepository.findTotalQuotePrescriptions(medicine.getId(), regionId, districtId, workPlaceId, field, startDate, endDate);
            System.out.println("11111111118");

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

    public List<AdminPrescriptionsMedicine> getAdminPrescriptionsByMedicineId(Long medicineId, Long regionId, Long districtId, Long workPlaceId, Field field, ContractType contractType, LocalDate startDate, LocalDate endDate) {
        List<AdminPrescriptionsMedicine> adminPrescriptionsMedicineList = new ArrayList<>();
//        Long written = medicineWithQuantityDoctorRepository.findTotalWritten(medicineId, contractType, regionId, districtId, workPlaceId, field, startDate, endDate);
//
//        AdminPrescriptionsMedicine adminPrescriptionsMedicine = new AdminPrescriptionsMedicine();
//        doctorReportDTO.setWritten(written);

        return adminPrescriptionsMedicineList;
    }
}
