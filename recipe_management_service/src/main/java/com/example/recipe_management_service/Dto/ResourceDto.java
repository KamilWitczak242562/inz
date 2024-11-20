package com.example.recipe_management_service.Dto;

import lombok.Data;

@Data
public class ResourceDto {
    private Long resourceId;
    private String name;
    private String description;
    private Double currentStock;
    private String unit;
    private Double reorderLevel;

}
