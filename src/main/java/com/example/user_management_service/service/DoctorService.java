package com.example.user_management_service.service;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.Preparation;
import com.example.user_management_service.model.Template;
import com.example.user_management_service.model.dto.PreparationDto;
import com.example.user_management_service.model.dto.TemplateDto;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.TemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-5:13 PM (GMT+5)
 */

@Service
@RequiredArgsConstructor
public class DoctorService {


    private final TemplateRepository templateRepository;

    private final MedicineRepository medicineRepository;


    public void saveTemplate(Long id, boolean save) {
        Template template = templateRepository.findById(id).orElse(null);
        if (template != null && save != template.isSaved()) {
            template.setSaved(save);
            templateRepository.save(template);
        }
    }

    public void saveTemplate(TemplateDto templateDto, boolean save) {
        Template template = convertToEntity(templateDto);
        Template existingTemplate = templateRepository.findById(template.getId()).orElse(null);
        if (existingTemplate != null && save != existingTemplate.isSaved()) {
            existingTemplate.setSaved(save);
            templateRepository.save(existingTemplate);
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

    private Template convertToEntity(TemplateDto templateDto) {
        Template template = new Template();
        template.setId(templateDto.getId());
        template.setName(templateDto.getName());
        template.setDiagnosis(templateDto.getDiagnosis());
        template.setPreparations(templateDto.getPreparations().stream().map(preparationDto -> {
            Preparation preparation = new Preparation();
            preparation.setName(preparationDto.getName());
            preparation.setAmount(preparationDto.getAmount());
            preparation.setQuantity(preparationDto.getQuantity());
            preparation.setTimesInDay(preparationDto.getTimesInDay());
            preparation.setDays(preparationDto.getDays());
            preparation.setType(preparationDto.getType());
            Medicine medicine = medicineRepository.findById(preparationDto.getMedicineId())
                    .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + preparationDto.getMedicineId()));
            preparation.setMedicine(medicine);

            return preparation;
        }).collect(Collectors.toList()));
        template.setNote(templateDto.getNote());
        template.setSaved(templateDto.isSaved());
        return template;
    }

}
