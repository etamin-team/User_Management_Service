package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-1/13/2025
 * By Sardor Tokhirov
 * Time-10:32 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastRecipeDTO {

    private UUID recipeId;
    private UserFullNameDTO doctorName;
    private WorkPlaceDTO workPlaceDTO;
    private LocalDate dateOfCreation;
    private List<Medicine> medicines;

}


