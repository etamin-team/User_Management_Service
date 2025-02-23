package com.example.user_management_service.repository;

import com.example.user_management_service.model.ContractMedicineAmount;
import com.example.user_management_service.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractMedicineAmountRepository extends JpaRepository<ContractMedicineAmount, Long> {

    @Query(value = "SELECT COALESCE(SUM(cma.amount), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contract_medicine_amounts cma ON m.contract_medicine_doctor_amount_id = cma.id",
            nativeQuery = true)
    Long getTotalContractMedicineDoctorAmount();

    @Query(value = "SELECT COALESCE(SUM(cma.amount), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contract_medicine_amounts cma ON m.contract_medicine_doctor_amount_id = cma.id " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "JOIN districts d ON u.district_id = d.id " +
            "JOIN regions r ON d.region_id = r.id " +
            "WHERE r.id = :regionId",
            nativeQuery = true)
    Long getTotalContractMedicineDoctorAmountByRegion(@Param("regionId") Long regionId);

    @Query(value = "SELECT COALESCE(SUM(cma.amount), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contract_medicine_amounts cma ON m.contract_medicine_doctor_amount_id = cma.id " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "WHERE c.doctor_id = :userId",
            nativeQuery = true)
    Long getTotalContractMedicineDoctorAmountByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT COALESCE(SUM(cma.amount), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contract_medicine_amounts cma ON m.contract_medicine_doctor_amount_id = cma.id " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "WHERE u.workplace_id = :workplaceId AND u.field_name = :fieldName",
            nativeQuery = true)
    Long getTotalContractMedicineDoctorAmountByWorkplaceAndField(
            @Param("workplaceId") Long workplaceId,
            @Param("fieldName") String fieldName);



    @Query(value = "SELECT COALESCE(SUM(cma.amount), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contract_medicine_amounts cma ON m.contract_medicine_doctor_amount_id = cma.id " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "WHERE  u.workplace_id = :workplaceId",
            nativeQuery = true)
    Long getTotalContractMedicineDoctorAmountByDistrictAndWorkplace(

            @Param("workplaceId") Long workplaceId);


    @Query(value = "SELECT COALESCE(SUM(cma.amount), 0) " +
            "FROM medicine_with_quantity_doctor m " +
            "JOIN contract_medicine_amounts cma ON m.contract_medicine_doctor_amount_id = cma.id " +
            "JOIN contracts c ON m.contract_id = c.contract_id " +
            "JOIN users u ON c.doctor_id = u.user_id " +
            "WHERE  u.district_id = :districtId",
            nativeQuery = true)
    Long getTotalContractMedicineDoctorAmountByDistrictId(

            @Param("districtId") Long districtId);

}