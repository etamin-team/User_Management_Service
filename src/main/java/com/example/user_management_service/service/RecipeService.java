package com.example.user_management_service.service;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.Preparation;
import com.example.user_management_service.model.Recipe;
import com.example.user_management_service.model.dto.PreparationDto;
import com.example.user_management_service.model.dto.RecipeDto;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-10:53 PM (GMT+5)
 */
@Service
public class RecipeService {

    private final MedicineRepository medicineRepository;
    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(MedicineRepository medicineRepository, RecipeRepository recipeRepository) {
        this.medicineRepository = medicineRepository;
        this.recipeRepository = recipeRepository;
    }


    public void saveRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setFirstName(recipeDto.getFirstName());
        recipe.setLastName(recipeDto.getLastName());
        recipe.setDateOfBirth(recipeDto.getDateOfBirth());
        recipe.setPhoneNumber(recipeDto.getPhoneNumber());
        recipe.setPhoneNumberPrefix(recipeDto.getPhoneNumberPrefix());
        recipe.setDiagnosis(recipeDto.getDiagnosis());
        recipe.setComment(recipeDto.getComment());
        recipe.setTelegramId(recipeDto.getTelegramId());
        recipe.setPreparations(
                recipeDto.getPreparations().stream().map(this::mapPreparationDtoToEntity).collect(Collectors.toList())
        );
        recipe.setDateCreation(LocalDate.now());
        recipeRepository.save(recipe);
    }

    private Preparation mapPreparationDtoToEntity(PreparationDto preparationDto) {
        Preparation preparation = new Preparation();
        preparation.setName(preparationDto.getName());
        preparation.setAmount(preparationDto.getAmount());
        preparation.setQuantity(preparationDto.getQuantity());
        preparation.setTimesInDay(preparationDto.getTimesInDay());
        preparation.setDays(preparationDto.getDays());
        preparation.setType(preparationDto.getType());


        Medicine medicine = medicineRepository.findById(preparationDto.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine with ID " + preparationDto.getMedicineId() + " not found"));
        preparation.setMedicine(medicine);

        return preparation;
    }
}