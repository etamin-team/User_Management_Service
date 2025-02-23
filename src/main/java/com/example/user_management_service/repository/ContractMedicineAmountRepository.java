package com.example.user_management_service.repository;

import com.example.user_management_service.model.ContractMedicineAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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


}