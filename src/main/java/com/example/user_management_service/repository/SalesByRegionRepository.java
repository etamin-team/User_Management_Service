package com.example.user_management_service.repository;

import com.example.user_management_service.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.user_management_service.model.SalesByRegion;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesByRegionRepository extends JpaRepository<SalesByRegion, Long> {
    void deleteBySales(Sales sales);
}