package com.example.user_management_service.repository;

import com.example.user_management_service.model.MedicineWithQuantityDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MedicineWithQuantityDoctorRepository  extends JpaRepository<MedicineWithQuantityDoctor, Long> {

}
