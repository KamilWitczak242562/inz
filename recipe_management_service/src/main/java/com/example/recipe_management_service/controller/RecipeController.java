package com.example.recipe_management_service.controller;

import com.example.recipe_management_service.model.Recipe;
import com.example.recipe_management_service.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recipe")
@AllArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRecipes() {
        List<Recipe> recipeList = recipeService.getAll();
        if (recipeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No recipes found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", recipeList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long id) {
        try {
            Recipe recipe = recipeService.getById(id);
            return ResponseEntity.ok(Map.of("response", recipe, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createRecipe(@RequestBody Recipe recipe) {
        if (recipe == null || recipe.getName() == null || recipe.getDescription() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid Recipe data", "ok", false));
        }
        Recipe newRecipe = recipeService.create(recipe);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newRecipe, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        try {
            if (recipe == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid Recipe data", "ok", false));
            }
            Recipe updatedRecipe = recipeService.update(id, recipe);
            return ResponseEntity.ok(Map.of("response", updatedRecipe, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        boolean isDeleted = recipeService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "Recipe deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "Recipe not found", "ok", false));
        }
    }

    @PostMapping("/{recipeId}/addResource")
    public ResponseEntity<?> addResourceToRecipe(@PathVariable Long recipeId, @RequestParam Long resourceId, @RequestParam Double quantity) {
        try {
            if (resourceId == null || quantity == null || quantity <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid resource data", "ok", false));
            }
            recipeService.addResourceToRecipe(recipeId, resourceId, quantity);
            return ResponseEntity.ok(Map.of("response", "Resource added successfully", "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{recipeId}/removeResource/{resourceId}")
    public ResponseEntity<?> removeResourceFromRecipe(@PathVariable Long recipeId, @PathVariable Long resourceId) {
        try {
            recipeService.removeResourceFromRecipe(recipeId, resourceId);
            return ResponseEntity.ok(Map.of("response", "Resource removed successfully", "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

}
