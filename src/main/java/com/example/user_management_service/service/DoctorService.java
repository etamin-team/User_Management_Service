package com.example.user_management_service.service;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.Preparation;
import com.example.user_management_service.model.Template;
import com.example.user_management_service.model.dto.DoctorDto;
import com.example.user_management_service.model.dto.PreparationDto;
import com.example.user_management_service.model.dto.TemplateDto;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RecipeRepository;
import com.example.user_management_service.repository.TemplateRepository;
import com.example.user_management_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;


    public void saveTemplate(Long id, boolean save) {
        Template template = templateRepository.findById(id).orElse(null);
        if (template != null && save != template.isSaved()) {
            template.setSaved(save);
            templateRepository.save(template);
        }
    }

    public void saveTemplate(TemplateDto templateDto) {
        Template template = convertToEntity(templateDto);
        template.setDoctorId(userRepository.findById(templateDto.getDoctorId()).orElseThrow());
        template.setId(templateDto.getId());
        template.setSaved(template.isSaved());
        templateRepository.save(template);
    }

    public void createTemplate(TemplateDto templateDto) {
        Template template = convertToEntity(templateDto);
        template.setDoctorId(userRepository.findById(templateDto.getDoctorId()).orElseThrow());
        templateRepository.save(template);
    }

    public void deleteTemplate(Long templateId) {
        templateRepository.deleteById(templateId);
    }

    public List<TemplateDto> getTemplates(Boolean saved, Boolean sortBy, String searchText, UUID doctorId) {
        List<Template> templates;

        if (searchText != null && !searchText.isEmpty()) {
            if (saved != null && saved) {
                templates = sortBy
                        ? templateRepository.findBySavedTrueAndSearchTextOrderByDiagnosisAsc(searchText, doctorId)
                        : templateRepository.findBySavedTrueAndSearchText(searchText, doctorId);
            } else if (saved != null && !saved) {
                templates = sortBy
                        ? templateRepository.findBySavedFalseAndSearchTextOrderByDiagnosisAsc(searchText, doctorId)
                        : templateRepository.findBySavedFalseAndSearchText(searchText, doctorId);
            } else {
                templates = sortBy
                        ? templateRepository.findAllBySearchTextOrderByDiagnosisAsc(searchText, doctorId)
                        : templateRepository.findAllBySearchText(searchText, doctorId);
            }
        } else {
            if (saved != null && saved) {
                templates = sortBy
                        ? templateRepository.findBySavedTrueOrderByDiagnosisAsc(doctorId)
                        : templateRepository.findBySavedTrue(doctorId);
            } else if (saved != null && !saved) {
                templates = sortBy
                        ? templateRepository.findBySavedFalseOrderByDiagnosisAsc(doctorId)
                        : templateRepository.findBySavedFalse(doctorId);
            } else {
                templates = sortBy
                        ? templateRepository.findAllByDoctorIdAsc(doctorId)
                        : templateRepository.findAllByDoctorId(doctorId);
            }
        }

        return templates.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private TemplateDto convertToDto(Template template) {
        return new TemplateDto(
                template.getId(),
                template.getName(),
                template.getDiagnosis(),
                template.getPreparations() != null
                        ? template.getPreparations().stream().map(this::convertToPreparationDto).collect(Collectors.toList())
                        : Collections.emptyList(),
                template.getNote(),
                template.getDoctorId().getUserId(),
                template.isSaved()
        );
    }

    private PreparationDto convertToPreparationDto(Preparation preparation) {
        return new PreparationDto(
                preparation.getName(),
                preparation.getAmount(),
                preparation.getQuantity(),
                preparation.getTimesInDay(),
                preparation.getDays(),
                preparation.getType(),
                preparation.getMedicine() != null ? preparation.getMedicine().getId() : null, preparation.getMedicine()
        );
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

    public List<Medicine> findMedicinesByInn(List<String> inn) {
        if (inn == null || inn.isEmpty()) {
            return Collections.emptyList();
        } else {
            return medicineRepository.findByInn(inn);
        }
    }
}
