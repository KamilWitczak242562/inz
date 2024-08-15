package com.example.resource_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "resourceId")
    @JsonBackReference
    private Resource resource;

    private Double quantity;
}
