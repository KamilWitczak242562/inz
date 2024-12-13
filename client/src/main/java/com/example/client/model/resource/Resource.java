package com.example.client.model.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    private Long resourceId;
    private String name;
    private String description;
    private Double currentStock;
    private String unit;
    private Double reorderLevel;

    public Resource(String name, String description, Double currentStock, String unit, Double reorderLevel) {
        this.name = name;
        this.description = description;
        this.currentStock = currentStock;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
    }
}
