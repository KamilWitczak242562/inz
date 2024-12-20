package com.example.client.model.recipe;

import lombok.Data;

import java.util.Map;

@Data
public class Recipe {
    private Long id;

    private String name;
    private String description;

    private Map<Long, Double> resourcesQuantities;

}
