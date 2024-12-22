package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Recipe;
import com.example.recipe_management_service.repository.RecipeRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecipeService implements ServiceTemplate<Recipe> {

    private final RecipeRepo recipeRepository;

    @Override
    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recipe not found with ID: " + id));
    }

    @Override
    public Recipe create(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe update(Long id, Recipe recipe) {
        Recipe existingRecipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recipe not found with ID: " + id));
        existingRecipe.setName(recipe.getName());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setResourcesQuantities(recipe.getResourcesQuantities());
        return recipeRepository.save(existingRecipe);
    }

    @Override
    public boolean delete(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }


    public void addResourceToRecipe(Long recipeId, Long resourceId, Double quantity) {
        Recipe recipe = getById(recipeId);
        if (recipe.getResourcesQuantities().containsKey(resourceId)) {
            throw new IllegalArgumentException("Resource is already part of the recipe.");
        }
        recipe.getResourcesQuantities().put(resourceId, quantity);
        recipeRepository.save(recipe);
    }

    public void removeResourceFromRecipe(Long recipeId, Long resourceId) {
        Recipe recipe = getById(recipeId);
        if (!recipe.getResourcesQuantities().containsKey(resourceId)) {
            throw new IllegalArgumentException("Resource is not part of the recipe.");
        }
        recipe.getResourcesQuantities().remove(resourceId);
        recipeRepository.save(recipe);
    }

}