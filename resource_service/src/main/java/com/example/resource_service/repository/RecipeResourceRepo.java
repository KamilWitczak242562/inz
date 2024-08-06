package com.example.resource_service.repository;

import com.example.resource_service.model.Recipe;
import com.example.resource_service.model.RecipeResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeResourceRepo extends JpaRepository<RecipeResource, Long> {
    List<RecipeResource> findByRecipe(Recipe recipe);
}
