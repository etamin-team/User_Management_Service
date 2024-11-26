package com.example.user_management_service.repository;

import com.example.user_management_service.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Date-11/21/2024
 * By Sardor Tokhirov
 * Time-3:18 PM (GMT+5)
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    // Find all countries by region ID
    List<Country> findByRegionId(Long regionId);

    // Find a country by name
    Country findByName(String name);
}
