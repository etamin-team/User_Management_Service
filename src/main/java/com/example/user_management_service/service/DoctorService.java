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
        template.setId(templateDto.getId());
        template.setSaved(save);
        templateRepository.save(template);
    }
    public void createTemplate(TemplateDto templateDto) {
        Template template = convertToEntity(templateDto);
        templateRepository.save(template);
    }

    public List<Template> getTemplates(Boolean saved, Boolean sortBy, String searchText) {
        if (searchText != null && !searchText.isEmpty()) {
            if (saved != null && saved) {
                return sortBy
                        ? templateRepository.findBySavedTrueAndSearchTextOrderByDiagnosisAsc(searchText)
                        : templateRepository.findBySavedTrueAndSearchText(searchText);
            } else if (saved != null && !saved) {
                return sortBy
                        ? templateRepository.findBySavedFalseAndSearchTextOrderByDiagnosisAsc(searchText)
                        : templateRepository.findBySavedFalseAndSearchText(searchText);
            } else {
                return sortBy
                        ? templateRepository.findAllBySearchTextOrderByDiagnosisAsc(searchText)
                        : templateRepository.findAllBySearchText(searchText);
            }
        } else {
            if (saved != null && saved) {
                return sortBy
                        ? templateRepository.findBySavedTrueOrderByDiagnosisAsc()
                        : templateRepository.findBySavedTrue();
            } else if (saved != null && !saved) {
                return sortBy
                        ? templateRepository.findBySavedFalseOrderByDiagnosisAsc()
                        : templateRepository.findBySavedFalse();
            } else {
                return sortBy
                        ? templateRepository.findAll(Sort.by(Sort.Order.asc("diagnosis")))
                        : templateRepository.findAll();
            }
        }
    }


    private Template convertToEntity(TemplateDto templateDto) {
        Template template = new Template();
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
