package com.example.user_management_service.service;

import com.example.user_management_service.model.Recipe;
import com.example.user_management_service.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-10:53 PM (GMT+5)
 */
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public void saveRecipe(Recipe recipe) {
        recipe.setDateCreation(LocalDate.now());
        recipeRepository.save(recipe);
    }
}