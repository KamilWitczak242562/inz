package com.example.resource_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "resources")
@Data
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    private String name;
    private String description;
    private Double currentStock;
    private String unit;
    private Double reorderLevel;

    @OneToMany(mappedBy = "resource")
    @JsonManagedReference
    List<RecipeResource> recipeResourceList;
}
