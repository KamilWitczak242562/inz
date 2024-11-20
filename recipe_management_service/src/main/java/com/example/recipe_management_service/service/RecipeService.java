package com.example.recipe_management_service.service;

import com.example.recipe_management_service.Dto.ResourceDto;
import com.example.recipe_management_service.model.Recipe;
import com.example.recipe_management_service.repository.RecipeRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecipeService implements ServiceTemplate<Recipe> {

    private final RecipeRepo recipeRepository;
    private final WebClient.Builder webClientBuilder;

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

    /***
     * REMEMBER TO CHANGE BASE URL AFTER DOCKER INFRA
     */
    private ResourceDto getResourceById(Long resourceId) {
        return webClientBuilder.baseUrl("http://localhost:8080/api/v1/resources/resource/")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/{id}").build(resourceId))
                .retrieve()
                .bodyToMono(ResourceDto.class)
                .block();
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

    public List<ResourceDto> fetchResourcesForRecipe(Long recipeId) {
        Recipe recipe = getById(recipeId);
        List<Long> resourceIds = recipe.getResourcesQuantities().keySet().stream().collect(Collectors.toList());
        return resourceIds.stream()
                .map(this::getResourceById)
                .collect(Collectors.toList());
    }
}