package com.example.resource_service.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class RecipeDto {
    private Long recipeId;

    private String name;
    private String description;
    private HashMap<String, Integer> resources;
}
