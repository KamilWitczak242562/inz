package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Recipe;
import com.example.recipe_management_service.repository.RecipeRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecipeService implements ServiceTemplate<Recipe> {

    private final RecipeRepo recipeRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recipe not found with ID: " + id));
    }

    @Override
    public Recipe create(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe update(Long id, Recipe recipe) {
        Recipe existingRecipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recipe not found with ID: " + id));
        existingRecipe.setName(recipe.getName());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setResourcesQuantities(recipe.getResourcesQuantities());
        return recipeRepository.save(existingRecipe);
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

    public void addResourceToRecipe(Long recipeId, Long resourceId, Double quantity) {
        Recipe recipe = getById(recipeId);
        if (recipe.getResourcesQuantities().containsKey(resourceId)) {
            throw new IllegalArgumentException("Resource is already part of the recipe.");
        }
        recipe.getResourcesQuantities().put(resourceId, quantity);
        recipeRepository.save(recipe);
    }

    public void removeResourceFromRecipe(Long recipeId, Long resourceId) {
        Recipe recipe = getById(recipeId);
        if (!recipe.getResourcesQuantities().containsKey(resourceId)) {
            throw new IllegalArgumentException("Resource is not part of the recipe.");
        }
        recipe.getResourcesQuantities().remove(resourceId);
        recipeRepository.save(recipe);
    }

    @Override
    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Recipe.class, false, true)
                .add(AuditEntity.revisionProperty("timestamp").ge(startDate.getTime()))
                .add(AuditEntity.revisionProperty("timestamp").le(endDate.getTime()));

        if (!"ALL".equalsIgnoreCase(revisionType)) {
            RevisionType revType = switch (revisionType.toUpperCase()) {
                case "INSERT" -> RevisionType.ADD;
                case "UPDATE" -> RevisionType.MOD;
                case "DELETE" -> RevisionType.DEL;
                default -> throw new IllegalArgumentException("Invalid revision type: " + revisionType);
            };
            query.add(AuditEntity.revisionType().eq(revType));
        }

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> {
                    Recipe recipe = (Recipe) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "recipe", recipe,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .collect(Collectors.toList());
    }

}