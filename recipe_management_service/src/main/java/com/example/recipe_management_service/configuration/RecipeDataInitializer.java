package com.example.recipe_management_service.configuration;

import com.example.recipe_management_service.model.Recipe;
import com.example.recipe_management_service.repository.RecipeRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class RecipeDataInitializer implements CommandLineRunner {
    private final RecipeRepo recipeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initRecipes();
    }

    private void initRecipes() {
        Recipe recipe1 = new Recipe();
        recipe1.setName("Recipe A");
        recipe1.setDescription("Description for Recipe A");
        recipe1.setResourcesQuantities(createResourceQuantities(Map.of(1L, 5.0, 2L, 10.0)));
        recipeRepository.save(recipe1);

        simulateModificationForRecipe(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setName("Recipe B");
        recipe2.setDescription("Description for Recipe B");
        recipe2.setResourcesQuantities(createResourceQuantities(Map.of(3L, 8.0, 4L, 15.0)));
        recipeRepository.save(recipe2);

        simulateModificationForRecipe(recipe2);
    }

    private Map<Long, Double> createResourceQuantities(Map<Long, Double> resources) {
        return new HashMap<>(resources);
    }

    private void simulateModificationForRecipe(Recipe recipe) {
        recipe.setDescription(recipe.getDescription() + " (Updated)");
        recipe.getResourcesQuantities().put(5L, 20.0);
        recipeRepository.save(recipe);
    }
}
