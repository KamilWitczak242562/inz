package com.example.resource_service.mapper;

import com.example.resource_service.dto.RecipeDto;
import com.example.resource_service.model.Recipe;
import com.example.resource_service.model.RecipeResource;
import com.example.resource_service.model.Resource;
import com.example.resource_service.repository.RecipeResourceRepo;
import com.example.resource_service.repository.ResourceRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeMapper {

    private final ResourceRepo resourceRepository;
    private final RecipeResourceRepo recipeResourceRepo;

    public RecipeMapper(ResourceRepo resourceRepository, RecipeResourceRepo recipeResourceRepo) {
        this.resourceRepository = resourceRepository;
        this.recipeResourceRepo = recipeResourceRepo;
    }

    public RecipeDto toDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        List<RecipeResource> recipeResources = recipeResourceRepo.findByRecipe(recipe);
        HashMap<String, Integer> resources = recipeResources.stream()
                .collect(Collectors.toMap(
                        r -> r.getResource().getName(), // Zakładamy, że Resource ma metodę getName
                        r -> r.getQuantity().intValue(), // Zakładamy, że quantity jest typu Double
                        (existing, replacement) -> existing, // W przypadku duplikatów zachowaj istniejącą wartość
                        HashMap::new
                ));

        RecipeDto dto = new RecipeDto();
        dto.setRecipeId(recipe.getRecipeId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setResources(resources);

        return dto;
    }

    public Recipe fromDto(RecipeDto dto) {
        if (dto == null) {
            return null;
        }

        Recipe recipe = new Recipe();
        recipe.setRecipeId(dto.getRecipeId());
        recipe.setName(dto.getName());
        recipe.setDescription(dto.getDescription());

        return recipe;
    }

    public List<RecipeResource> fromDtoToRecipeResources(RecipeDto dto, Recipe recipe) {
        if (dto == null || recipe == null) {
            return null;
        }

        Map<String, Resource> resourceMap = resourceRepository.findAllByNameIn(dto.getResources().keySet()).stream()
                .collect(Collectors.toMap(Resource::getName, r -> r));

        return dto.getResources().entrySet().stream()
                .map(entry -> {
                    RecipeResource recipeResource = new RecipeResource();
                    recipeResource.setRecipe(recipe);
                    recipeResource.setResource(resourceMap.get(entry.getKey())); // Mapowanie nazwy zasobu na obiekt Resource
                    recipeResource.setQuantity(entry.getValue().doubleValue()); // Zakładam, że value jest typu Integer

                    return recipeResource;
                })
                .collect(Collectors.toList());
    }
}
