package com.example.resource_service.controller;

import com.example.resource_service.model.RecipeResource;
import com.example.resource_service.service.RecipeResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipeResource")
@AllArgsConstructor
public class RecipeResourceController {
    private final RecipeResourceService recipeResourceService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRecipeResources() {
        List<RecipeResource> recipeResourceList = recipeResourceService.getAll();
        return ResponseEntity.ok(recipeResourceList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeResourceById(@PathVariable Long id) {
        RecipeResource recipeResource = recipeResourceService.getById(id);
        return ResponseEntity.ok(recipeResource);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createRecipeResource(@RequestBody RecipeResource recipeResource) {
        RecipeResource newRecipeResource = recipeResourceService.create(recipeResource);
        return ResponseEntity.ok(newRecipeResource);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateRecipeResource(@PathVariable Long id, @RequestBody RecipeResource recipeResource) {
        RecipeResource updatedRecipeResource = recipeResourceService.update(id, recipeResource);
        return ResponseEntity.ok(updatedRecipeResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipeResource(@PathVariable Long id) {
        boolean isDeleted = recipeResourceService.delete(id);
        return ResponseEntity.ok(isDeleted);
    }
}
