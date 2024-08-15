package com.example.resource_service.service;

import com.example.resource_service.model.RecipeResource;
import com.example.resource_service.repository.RecipeResourceRepo;
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
class RecipeResourceServiceTest {

    @Mock
    private RecipeResourceRepo recipeResourceRepo;

    @InjectMocks
    private RecipeResourceService recipeResourceService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        recipeResourceService = new RecipeResourceService(recipeResourceRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreate() {
        RecipeResource recipeResource = new RecipeResource();
        when(recipeResourceRepo.save(recipeResource)).thenReturn(recipeResource);

        RecipeResource result = recipeResourceService.create(recipeResource);

        assertNotNull(result);
        assertEquals(recipeResource, result);
    }

    @Test
    void testDeleteSuccess() {
        when(recipeResourceRepo.existsById(1L)).thenReturn(true);

        boolean result = recipeResourceService.delete(1L);

        assertTrue(result);
        verify(recipeResourceRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteFailure() {
        when(recipeResourceRepo.existsById(1L)).thenReturn(false);

        boolean result = recipeResourceService.delete(1L);

        assertFalse(result);
        verify(recipeResourceRepo, never()).deleteById(1L);
    }

    @Test
    void testUpdate() {
        RecipeResource existingRecipeResource = new RecipeResource();
        RecipeResource updatedRecipeResource = new RecipeResource();
        when(recipeResourceRepo.findById(1L)).thenReturn(Optional.of(existingRecipeResource));
        when(recipeResourceRepo.save(updatedRecipeResource)).thenReturn(updatedRecipeResource);

        RecipeResource result = recipeResourceService.update(1L, updatedRecipeResource);

        assertNotNull(result);
        assertEquals(updatedRecipeResource, result);
    }

    @Test
    void testGetById() {
        RecipeResource recipeResource = new RecipeResource();
        when(recipeResourceRepo.findById(1L)).thenReturn(Optional.of(recipeResource));

        RecipeResource result = recipeResourceService.getById(1L);

        assertNotNull(result);
        assertEquals(recipeResource, result);
    }

    @Test
    void testGetAll() {
        RecipeResource recipeResource1 = new RecipeResource();
        RecipeResource recipeResource2 = new RecipeResource();
        when(recipeResourceRepo.findAll()).thenReturn(Arrays.asList(recipeResource1, recipeResource2));

        List<RecipeResource> result = recipeResourceService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(recipeResource1));
        assertTrue(result.contains(recipeResource2));
    }
}
