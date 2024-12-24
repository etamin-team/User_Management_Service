package com.example.user_management_service.service;

import com.example.user_management_service.model.Template;
import com.example.user_management_service.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-5:13 PM (GMT+5)
 */

@Service
@RequiredArgsConstructor
public class DoctorService {


    private final TemplateRepository templateRepository;


    public void saveTemplate(Long id, boolean save) {
        Template template = templateRepository.findById(id).orElse(null);
        if (template != null && save != template.isSaved()){
            template.setSaved(save);
            templateRepository.save(template);
        }
    }

    public void saveTemplate(Template template, boolean save) {
        Template template1 = templateRepository.findById(template.getId()).orElse(null);
        if (template != null && save != template.isSaved()){
            template1.setSaved(save);
            templateRepository.save(template1);
        }
    }

    public List<Template> getTemplates(Boolean saved, Boolean sortBy) {
        if (saved != null && saved) {
            if (sortBy) {
                return templateRepository.findBySavedTrueOrderByDiagnosisAsc();
            } else {
                return templateRepository.findBySavedTrue();
            }
        } else if (saved != null && !saved) {
            if (sortBy) {
                return templateRepository.findBySavedFalseOrderByDiagnosisAsc();
            } else {
                return templateRepository.findBySavedFalse();
            }
        } else {
            if (sortBy) {
                return templateRepository.findAll(Sort.by(Sort.Order.asc("diagnosis")));
            } else {
                return templateRepository.findAll();
            }
        }
    }


}
