package com.example.recipe_management_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.Map;

@Entity
@Table(name = "recipes")
@Data
@Audited
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ElementCollection
    @CollectionTable(name = "recipe_resources", joinColumns = @JoinColumn(name = "recipe_id"))
    @MapKeyColumn(name = "resource_id")
    @Column(name = "quantity")
    private Map<Long, Double> resourcesQuantities;

}
