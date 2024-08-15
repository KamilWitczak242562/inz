package com.example.resource_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "recipes")
@Data
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;

    private String name;
    private String description;

    @OneToMany(mappedBy = "recipe")
    List<RecipeResource> recipeResourceList;
}