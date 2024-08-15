package com.example.resource_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recipe_resources")
@Data
public class RecipeResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipeId")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "resourceId")
    private Resource resource;

    private Double quantity;
}
