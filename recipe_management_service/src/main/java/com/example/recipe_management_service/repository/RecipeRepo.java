package com.example.recipe_management_service.repository;

import com.example.recipe_management_service.model.MainTank;
import com.example.recipe_management_service.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, Long> {
}
