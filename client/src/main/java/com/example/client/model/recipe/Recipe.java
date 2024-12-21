package com.example.client.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    private Long id;

    private String name;
    private String description;

    private Map<Long, Double> resourcesQuantities;

}
