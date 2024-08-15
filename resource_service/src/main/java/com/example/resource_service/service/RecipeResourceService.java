package com.example.resource_service.service;

import com.example.resource_service.model.RecipeResource;
import com.example.resource_service.repository.RecipeResourceRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecipeResourceService implements ServiceTemplate<RecipeResource> {
    private final RecipeResourceRepo recipeResourceRepo;

    @Override
    public RecipeResource create(RecipeResource entity) {
        return recipeResourceRepo.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (recipeResourceRepo.existsById(id)) {
            recipeResourceRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public RecipeResource update(Long id, RecipeResource entity) {
        RecipeResource recipeResourceToChange = recipeResourceRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such recipe resource"));
        recipeResourceToChange.setRecipe(entity.getRecipe());
        recipeResourceToChange.setResource(entity.getResource());
        recipeResourceToChange.setQuantity(entity.getQuantity());
        return recipeResourceRepo.save(recipeResourceToChange);
    }

    @Override
    public RecipeResource getById(Long id) {
        return recipeResourceRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such recipe resource"));
    }

    @Override
    public List<RecipeResource> getAll() {
        return recipeResourceRepo.findAll();
    }
}
