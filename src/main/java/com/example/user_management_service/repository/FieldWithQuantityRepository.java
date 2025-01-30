package com.example.user_management_service.repository;

import com.example.user_management_service.model.FieldWithQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldWithQuantityRepository extends JpaRepository<FieldWithQuantity, Long> {
}
