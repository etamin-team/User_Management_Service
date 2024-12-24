package com.example.user_management_service.repository;

import com.example.user_management_service.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-10:51 PM (GMT+5)
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    // Custom query methods can be added here if necessary
}