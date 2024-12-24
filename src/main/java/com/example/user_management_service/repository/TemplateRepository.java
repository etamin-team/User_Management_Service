package com.example.user_management_service.repository;

import com.example.user_management_service.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-5:23 PM (GMT+5)
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {


    List<Template> findBySavedTrue();
    List<Template> findBySavedFalse();

    List<Template> findBySavedTrueOrderByDiagnosisAsc();
    List<Template> findBySavedFalseOrderByDiagnosisAsc();
}