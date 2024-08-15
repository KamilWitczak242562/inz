package com.example.resource_service.controller;

import com.example.resource_service.model.RecipeResource;
import com.example.resource_service.service.RecipeResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recipeResource")
@AllArgsConstructor
public class RecipeResourceController {
    private final RecipeResourceService recipeResourceService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRecipeResources() {
        List<RecipeResource> recipeResourceList = recipeResourceService.getAll();
        return ResponseEntity.ok().body(Map.of("response", recipeResourceList,
                "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeResourceById(@PathVariable Long id) {
        RecipeResource recipeResource = recipeResourceService.getById(id);
        return ResponseEntity.ok().body(Map.of("response", recipeResource,
                "ok", true));
    }

    @PostMapping("/new")
    public ResponseEntity<?> createRecipeResource(@RequestBody RecipeResource recipeResource) {
        RecipeResource newRecipeResource = recipeResourceService.create(recipeResource);
        return ResponseEntity.ok().body(Map.of("response", newRecipeResource,
                "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateRecipeResource(@PathVariable Long id, @RequestBody RecipeResource recipeResource) {
        RecipeResource updatedRecipeResource = recipeResourceService.update(id, recipeResource);
        return ResponseEntity.ok().body(Map.of("response", updatedRecipeResource,
                "ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipeResource(@PathVariable Long id) {
        boolean isDeleted = recipeResourceService.delete(id);
        return ResponseEntity.ok().body(Map.of("response", isDeleted,
                "ok", true));
    }
}
