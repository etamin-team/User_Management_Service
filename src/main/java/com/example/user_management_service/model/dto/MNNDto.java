package com.example.user_management_service.model.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Date-8/18/2025
 * By Sardor Tokhirov
 * Time-6:07 PM (GMT+5)
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MNNDto {
    private Long id;

    private String name;

    private String latinName;

    private String combination;


    private String type;

    private String dosage;


    private String wm_ru;

    private String pharmacotherapeuticGroup ;

    private List<Long> medicineIds;
}
