package com.example.resource_service.controller;

import com.example.resource_service.model.Recipe;
import com.example.resource_service.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/recipe")
@AllArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRecipes() {
        List<Recipe> recipeList = recipeService.getAll();
        return ResponseEntity.ok(recipeList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getById(id);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createRecipe(@RequestBody Recipe recipe) {
        Recipe newRecipe = recipeService.create(recipe);
        return ResponseEntity.ok(newRecipe);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        Recipe updatedRecipe = recipeService.update(id, recipe);
        return ResponseEntity.ok(updatedRecipe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        boolean isDeleted = recipeService.delete(id);
        return ResponseEntity.ok(isDeleted);
    }
}
