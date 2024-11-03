package com.example.recipe_management_service.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Pump extends Block{
    private Integer rpm;
    private Boolean circulation;
}
