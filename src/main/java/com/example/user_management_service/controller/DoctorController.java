package com.example.user_management_service.controller;

import com.example.user_management_service.model.Recipe;
import com.example.user_management_service.model.Template;
import com.example.user_management_service.model.dto.RecipeDto;
import com.example.user_management_service.model.dto.TemplateDto;
import com.example.user_management_service.service.DoctorService;
import com.example.user_management_service.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-5:20 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/doctor")
@CrossOrigin(origins = "*")
public class DoctorController {


    private final DoctorService doctorService;
    private final RecipeService recipeService;

    @Autowired
    public DoctorController(DoctorService doctorService, RecipeService recipeService) {
        this.doctorService = doctorService;
        this.recipeService = recipeService;
    }

    @PostMapping("/create-template")
    public void createTemplate(@RequestBody TemplateDto templateDto, @RequestParam(defaultValue = "false") boolean save) {
        doctorService.createTemplate(templateDto, save);
    }

    @PutMapping("/update-template")
    public void updateTemplate(@RequestBody TemplateDto templateDto, @RequestParam(defaultValue = "false") boolean save) {
        doctorService.saveTemplate(templateDto, save);
    }

    @PostMapping("/save-template/{id}")
    public void saveTemplate(@PathVariable Long id, @RequestParam boolean save) {
        doctorService.saveTemplate(id, save);
    }

    @GetMapping("/templates")
    public List<Template> getTemplates(
            @RequestParam(required = false) Boolean saved,
            @RequestParam(required = false, defaultValue = "false") Boolean sortBy,
            @RequestParam(required = false) String searchText) {
        return doctorService.getTemplates(saved, sortBy, searchText);
    }


    @PostMapping("/save-recipe")
    public void saveRecipe(@RequestBody RecipeDto recipe) {
        recipeService.saveRecipe(recipe);
    }
}
