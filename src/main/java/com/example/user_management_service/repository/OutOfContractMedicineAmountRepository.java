//package com.example.user_management_service.repository;
//
//import com.example.user_management_service.model.OutOfContractMedicineAmount;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface OutOfContractMedicineAmountRepository extends JpaRepository<OutOfContractMedicineAmount, Long> {
//
//    @Query(value = """
//            SELECT *
//            FROM out_of_contract_medicine_amounts o
//            WHERE o.doctor_id = :doctorId
//            """, nativeQuery = true)
//    Optional<List<OutOfContractMedicineAmount>> findAllForDoctorThisMonth(@Param("doctorId") UUID doctorId);
//
//}
