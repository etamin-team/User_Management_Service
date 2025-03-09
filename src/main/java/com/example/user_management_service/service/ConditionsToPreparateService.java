package com.example.user_management_service.service;

import com.example.user_management_service.exception.DataBaseException;
import com.example.user_management_service.model.ConditionsToPreparate;
import com.example.user_management_service.model.dto.ConditionsToPreparateDto;
import com.example.user_management_service.model.dto.PercentageValDto;
import com.example.user_management_service.repository.ConditionsToPreparateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Date-2/21/2025
 * By Sardor Tokhirov
 * Time-2:31 PM (GMT+5)
 */
@Service
public class ConditionsToPreparateService {

    private final ConditionsToPreparateRepository repository;

    public ConditionsToPreparateService(ConditionsToPreparateRepository repository) {
        this.repository = repository;
    }

    public ConditionsToPreparateDto save(ConditionsToPreparateDto dto) {
        ConditionsToPreparate entity = mapToEntity(dto);
        entity = repository.save(entity);
        return mapToDto(entity);
    }

    public List<ConditionsToPreparateDto> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ConditionsToPreparateDto getById(Long id) {
        Optional<ConditionsToPreparate> entity = repository.findById(id);
        return entity.map(this::mapToDto).orElse(null);
    }

    public ConditionsToPreparateDto update(Long id, ConditionsToPreparateDto dto) {
        if (!repository.existsById(id)) {
            throw new DataBaseException("Entity not found with id: " + id);
        }
        ConditionsToPreparate entity = mapToEntity(dto);
        entity.setId(id);
        ConditionsToPreparate entity2 = repository.save(entity);
        return mapToDto(entity2);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // **Mapper Methods**
    private ConditionsToPreparateDto mapToDto(ConditionsToPreparate entity) {
        return new ConditionsToPreparateDto(
                entity.getId(),
                entity.getMinPercentage(),
                entity.getMinPercentageVal(),
                entity.getMaxPercentage(),
                entity.getMaxPercentageVal(),
                entity.getPercentageVals().stream()
                        .map(pv -> new PercentageValDto(pv.getMinPercentage(),
                                pv.getMaxPercentage(), pv.getPercentageVal()))
                        .collect(Collectors.toList()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getSu(),
                entity.getSb(),
                entity.getGz(),
                entity.getKb()
        );
    }

    private ConditionsToPreparate mapToEntity(ConditionsToPreparateDto dto) {
        ConditionsToPreparate entity = new ConditionsToPreparate();
        entity.setId(dto.getId());
        entity.setMinPercentage(dto.getMinPercentage());
        entity.setMinPercentageVal(dto.getMinPercentageVal());
        entity.setMaxPercentage(dto.getMaxPercentage());
        entity.setMaxPercentageVal(dto.getMaxPercentageVal());
        entity.setPercentageVals(dto.getPercentageVals().stream()
                .map(pv -> new com.example.user_management_service.model.PercentageVal(
                        pv.getMinPercentage(),
                        pv.getMaxPercentage(), pv.getPercentageVal()))
                .collect(Collectors.toList()));
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setSu(dto.getSu());
        entity.setSb(dto.getSb());
        entity.setGz(dto.getGz());
        entity.setKb(dto.getKb());
        return entity;
    }
}