package com.example.resource_service.service;

import com.example.resource_service.model.Recipe;
import com.example.resource_service.repository.RecipeRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepo recipeRepo;

    @InjectMocks
    private RecipeService recipeService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        recipeService = new RecipeService(recipeRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreate() {
        Recipe recipe = new Recipe();
        when(recipeRepo.save(recipe)).thenReturn(recipe);

        Recipe result = recipeService.create(recipe);

        assertNotNull(result);
        assertEquals(recipe, result);
    }

    @Test
    void testDeleteSuccess() {
        when(recipeRepo.existsById(1L)).thenReturn(true);

        boolean result = recipeService.delete(1L);

        assertTrue(result);
        verify(recipeRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteFailure() {
        when(recipeRepo.existsById(1L)).thenReturn(false);

        boolean result = recipeService.delete(1L);

        assertFalse(result);
        verify(recipeRepo, never()).deleteById(1L);
    }

    @Test
    void testUpdate() {
        Recipe existingRecipe = new Recipe();
        Recipe updatedRecipe = new Recipe();
        when(recipeRepo.findById(1L)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepo.save(updatedRecipe)).thenReturn(updatedRecipe);

        Recipe result = recipeService.update(1L, updatedRecipe);

        assertNotNull(result);
        assertEquals(updatedRecipe, result);
    }

    @Test
    void testGetById() {
        Recipe recipe = new Recipe();
        when(recipeRepo.findById(1L)).thenReturn(Optional.of(recipe));

        Recipe result = recipeService.getById(1L);

        assertNotNull(result);
        assertEquals(recipe, result);
    }

    @Test
    void testGetAll() {
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();
        when(recipeRepo.findAll()).thenReturn(Arrays.asList(recipe1, recipe2));

        List<Recipe> result = recipeService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(recipe1));
        assertTrue(result.contains(recipe2));
    }
}
