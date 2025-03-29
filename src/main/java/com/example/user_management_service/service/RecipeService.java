package com.example.user_management_service.service;

import com.example.user_management_service.exception.ContractNotFoundException;
import com.example.user_management_service.exception.DoctorContractException;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.repository.ContractRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.example.user_management_service.repository.RecipeRepository;
import com.example.user_management_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;


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
    private final ContractService contractService;
    ;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ContractRepository contractRepository;
    private RegistrationService registrationService;


    public void saveRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setFirstName(recipeDto.getFirstName());
        recipe.setLastName(recipeDto.getLastName());
        recipe.setDateOfBirth(recipeDto.getDateOfBirth());
        recipe.setPhoneNumber(recipeDto.getPhoneNumber());
        recipe.setPhoneNumberPrefix(recipeDto.getPhoneNumberPrefix());
        recipe.setDiagnosis(recipeDto.getDiagnosis());
        recipe.setComment(recipeDto.getComment());

        List<Preparation> preparations = recipeDto.getPreparations().stream()
                .map(this::mapPreparationDtoToEntity)
                .collect(Collectors.toList());

        recipe.setPreparations(preparations);
        recipe.setDateCreation(LocalDateTime.now());
        User doctor = userRepository.findById(recipeDto.getDoctorId()).orElseThrow(() -> new DoctorContractException("Doctor not found"));
        recipe.setDoctorId(doctor);
        System.out.println("---------------------------------------");
        ContractType contractType = contractRepository.findActiveContractByDoctorId(doctor.getUserId()).orElse(new Contract()).getContractType();
        System.out.println(contractType.name());
        recipe.setContractType(contractType==null?ContractType.RECIPE:contractType);
        recipeRepository.save(recipe);
        if (preparations!=null && preparations.size()>0) {
            List<Long> medicineIds = preparations.stream()
                    .map(Preparation::getMedicine)
                    .filter(Objects::nonNull)  // Ensure no null medicines
                    .map(Medicine::getId)
                    .collect(Collectors.toList());

            contractService.saveContractMedicineAmount(recipe.getDoctorId().getUserId(), medicineIds);
        }
    }

    private Preparation mapPreparationDtoToEntity(PreparationDto preparationDto) {
        if (preparationDto==null){return null;}
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
            String firstName, String lastName, String middleName, Long district, Field category,
            Long regionId, Long medicineId, LocalDate startDate, LocalDate endDate, UUID doctorId) {

        return recipeRepository.findRecipesByFilters(firstName, lastName, middleName,regionId, district,medicineId,category , startDate, endDate)
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
                recipe.getPreparations().stream().map((preparation -> (
                        preparation.getMedicine()
                ))).toList()
        );
    }

    private WorkPlaceDTO convertToDTO(WorkPlace workPlace) {
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


    private UserFullNameDTO convertToUserFullNameDTO(User user) {
        return new UserFullNameDTO(user.getFirstName(), user.getLastName(), user.getMiddleName());
    }

    public Page<RecipeDto> filterRecipes(String nameQuery, Long regionId, Long districtId, Long medicineId, Field doctorField,
                                         LocalDate lastAnalysisFrom, LocalDate lastAnalysisTo,UUID doctorId, int page, int size) {
        String[] filteredParts = prepareNameParts(nameQuery);

        // Extract name components (first, second, third name parts)
        String name1 = filteredParts.length > 0 ? filteredParts[0].toLowerCase() : "";
        String name2 = filteredParts.length > 1 ? filteredParts[1].toLowerCase() : name1;
        String name3 = filteredParts.length > 2 ? filteredParts[2].toLowerCase() : name1;

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());

        // Fetch paged results
        Page<Recipe> recipes = recipeRepository.findRecipesByFilters(name1, name2, name3, regionId, districtId,medicineId,
                 doctorField, lastAnalysisFrom,
                lastAnalysisTo, pageable);

        System.out.println("-----------------------------------------------------------------------");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("-----------------------Recipe------------------------------------------");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("-----------------------------------------------------------------------");
        return recipes.map(this::convertToDto);
    }


    private RecipeDto convertToDto(Recipe recipe) {
        return new RecipeDto(
                recipe.getDoctorId().getUserId(),
                recipe.getFirstName(),
                recipe.getLastName(),
                recipe.getDateOfBirth(),
                recipe.getPhoneNumber(),
                recipe.getPhoneNumberPrefix(),
                recipe.getDateCreation(),
                recipe.getDiagnosis(),
                recipe.getComment(),
                recipe.getDoctorId().getDistrict()!=null? recipe.getDoctorId().getDistrict().getId():null,
                recipe.getContractType(),
                recipe.getPreparations().stream().map(this::convertPreparationToDto).collect(Collectors.toList()),
                userService.convertToDTO(recipe.getDoctorId())
        );
    }

    private PreparationDto convertPreparationToDto(Preparation preparation) {
        if (preparation==null){return new PreparationDto();}
        return new PreparationDto(
                preparation.getName(),
                preparation.getAmount(),
                preparation.getQuantity(),
                preparation.getTimesInDay(),
                preparation.getDays(),
                preparation.getType(),
                preparation.getMedicine()!=null?   preparation.getMedicine().getId():null, // Medicine ID
                preparation.getMedicine() // Medicine entity itself (if required)
        );
    }

    private String[] prepareNameParts(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return new String[0]; // Return empty array if no name query is provided
        }

        String[] nameParts = nameQuery.split(" ");
        List<String> cleanParts = new ArrayList<>();
        for (String part : nameParts) {
            if (part != null && !part.trim().isEmpty()) {
                cleanParts.add(part.trim());
            }
        }

        return cleanParts.toArray(new String[0]);
    }

}
