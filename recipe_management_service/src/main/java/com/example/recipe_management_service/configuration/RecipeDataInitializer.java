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
        recipe1.setDescription("Description of Recipe A");
        recipe1.setResourcesQuantities(createResourceQuantities(Map.of(
                1L, 50.0,
                2L, 30.0
        )));
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setName("Recipe B");
        recipe2.setDescription("Description of Recipe B");
        recipe2.setResourcesQuantities(createResourceQuantities(Map.of(
                1L, 20.0,
                3L, 20.0
        )));
        recipeRepository.save(recipe2);

        Recipe recipe3 = new Recipe();
        recipe3.setName("Recipe C");
        recipe3.setDescription("Description of Recipe C");
        recipe3.setResourcesQuantities(createResourceQuantities(Map.of(
                1L, 15.0,
                4L, 15.0
        )));
        recipeRepository.save(recipe3);
    }

    private Map<Long, Double> createResourceQuantities(Map<Long, Double> resources) {
        return new HashMap<>(resources);
    }
}
