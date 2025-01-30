package com.example.user_management_service.service;

import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RecipeRepository;
import com.example.user_management_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-10:53 PM (GMT+5)
 */
@Service
@AllArgsConstructor
public class RecipeService {

    private final MedicineRepository medicineRepository;
    private final RecipeRepository recipeRepository;
    private final ContractService contractService;;


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

        List<Preparation> preparations = recipeDto.getPreparations().stream()
                .map(this::mapPreparationDtoToEntity)
                .collect(Collectors.toList());

        recipe.setPreparations(preparations);
        recipe.setDateCreation(LocalDate.now());
        recipeRepository.save(recipe);

        List<Long> medicineIds = preparations.stream()
                .map(Preparation::getMedicine)
                .filter(Objects::nonNull)  // Ensure no null medicines
                .map(Medicine::getId)
                .collect(Collectors.toList());

        contractService.saveContractMedicineAmount(recipe.getDoctorId().getUserId(), medicineIds);
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

    public List<LastRecipeDTO> getRecipes(
            String firstName, String lastName, String middleName, String district, Field category,
            String specialty, Long medicineId, LocalDate startDate, LocalDate endDate) {

        return recipeRepository.findRecipesByFilters(firstName, lastName, middleName, district, category, specialty, startDate, endDate, medicineId)
                .stream()
                .map(this::mapToLastRecipeDTO)
                .collect(Collectors.toList());
    }



    private LastRecipeDTO mapToLastRecipeDTO(Recipe recipe) {

        return new LastRecipeDTO(
                recipe.getRecipeId(),
                convertToUserFullNameDTO(recipe.getDoctorId()),
                convertToDTO(recipe.getDoctorId().getWorkplace()),
                recipe.getDateCreation(),
                recipe.getPreparations().stream().map(Preparation::getName).toList()
        );
    }
    public WorkPlaceDTO convertToDTO(WorkPlace workPlace) {
        return new WorkPlaceDTO(
                workPlace.getId(),
                workPlace.getName(),
                workPlace.getAddress(),
                workPlace.getDescription(),
                workPlace.getPhone(),
                workPlace.getEmail(),
                workPlace.getMedicalInstitutionType(), // Include MedicalInstitutionType here
                workPlace.getChiefDoctor() != null ? workPlace.getChiefDoctor().getUserId() : null,
                workPlace.getDistrict() != null ? workPlace.getDistrict().getId() : null
        );

    }
    private UserFullNameDTO convertToUserFullNameDTO(User user){
        return new UserFullNameDTO(user.getFirstName(), user.getLastName(), user.getMiddleName());
    }
}
