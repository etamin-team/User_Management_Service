package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Date-1/2/2025
 * By Sardor Tokhirov
 * Time-4:36 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDto {
    private Long id;
    private String name;
    private String diagnosis;
    private List<PreparationDto> preparations;
    private String note;
    private boolean saved;
}