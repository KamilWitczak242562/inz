package com.example.resource_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.example.resource_service.dto.RecipeDto;
import com.example.resource_service.model.Recipe;
import com.example.resource_service.model.RecipeResource;
import com.example.resource_service.model.Resource;
import com.example.resource_service.repository.RecipeResourceRepo;
import com.example.resource_service.repository.ResourceRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class RecipeMapperTest {
    @InjectMocks
    private RecipeMapper recipeMapper;

    @Mock
    private ResourceRepo resourceRepository;

    @Mock
    private RecipeResourceRepo recipeResourceRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldConvertRecipeToDto() {
        // Given
        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);
        recipe.setName("Pasta");
        recipe.setDescription("Delicious pasta");

        Resource resource1 = new Resource();
        resource1.setName("Tomato");

        Resource resource2 = new Resource();
        resource2.setName("Basil");

        RecipeResource recipeResource1 = new RecipeResource();
        recipeResource1.setResource(resource1);
        recipeResource1.setQuantity(2.0);

        RecipeResource recipeResource2 = new RecipeResource();
        recipeResource2.setResource(resource2);
        recipeResource2.setQuantity(1.0);

        List<RecipeResource> recipeResources = Arrays.asList(recipeResource1, recipeResource2);

        when(recipeResourceRepo.findByRecipe(recipe)).thenReturn(recipeResources);

        // When
        RecipeDto dto = recipeMapper.toDto(recipe);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getRecipeId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Pasta");
        assertThat(dto.getDescription()).isEqualTo("Delicious pasta");
        assertThat(dto.getResources()).containsEntry("Tomato", 2)
                .containsEntry("Basil", 1);
    }

    @Test
    void shouldConvertDtoToRecipe() {
        // Given
        RecipeDto dto = new RecipeDto();
        dto.setRecipeId(1L);
        dto.setName("Pasta");
        dto.setDescription("Delicious pasta");

        // When
        Recipe recipe = recipeMapper.fromDto(dto);

        // Then
        assertThat(recipe).isNotNull();
        assertThat(recipe.getRecipeId()).isEqualTo(1L);
        assertThat(recipe.getName()).isEqualTo("Pasta");
        assertThat(recipe.getDescription()).isEqualTo("Delicious pasta");
    }

    @Test
    void shouldConvertDtoToRecipeResources() {
        // Given
        RecipeDto dto = new RecipeDto();
        dto.setRecipeId(1L);
        dto.setName("Pasta");
        dto.setDescription("Delicious pasta");
        HashMap<String, Integer> resources = new HashMap<>();
        resources.put("Tomato", 2);
        resources.put("Basil", 1);
        dto.setResources(resources);

        Resource tomato = new Resource();
        tomato.setName("Tomato");

        Resource basil = new Resource();
        basil.setName("Basil");

        when(resourceRepository.findAllByNameIn(Set.of("Tomato", "Basil"))).thenReturn(List.of(tomato, basil));

        Recipe recipe = recipeMapper.fromDto(dto);

        // When
        List<RecipeResource> recipeResources = recipeMapper.fromDtoToRecipeResources(dto, recipe);

        // Then
        assertThat(recipeResources).hasSize(2);
        assertThat(recipeResources).anyMatch(rr -> rr.getResource().getName().equals("Tomato") && rr.getQuantity().equals(2.0));
        assertThat(recipeResources).anyMatch(rr -> rr.getResource().getName().equals("Basil") && rr.getQuantity().equals(1.0));
    }

}