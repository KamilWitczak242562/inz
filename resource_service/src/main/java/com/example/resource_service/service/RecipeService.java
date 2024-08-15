package com.example.resource_service.service;

import com.example.resource_service.model.Recipe;
import com.example.resource_service.model.RecipeResource;
import com.example.resource_service.model.Resource;
import com.example.resource_service.repository.RecipeRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecipeService implements ServiceTemplate<Recipe> {
    private final RecipeRepo recipeRepository;

    @Override
    public Recipe create(Recipe entity) {
        return recipeRepository.save(entity);
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

    @Override
    public Recipe update(Long id, Recipe entity) {
        Recipe recipeToChange = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such recipe"));
        recipeToChange.setName(entity.getName());
        recipeToChange.setDescription(entity.getDescription());
        recipeToChange.setRecipeResourceList(entity.getRecipeResourceList());
        return recipeRepository.save(recipeToChange);
    }

    @Override
    public Recipe getById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such recipe"));
    }

    @Override
    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }
}
