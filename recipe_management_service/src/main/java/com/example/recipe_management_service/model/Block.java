package com.example.recipe_management_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "blocks")
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @ManyToMany(mappedBy = "blocks")
    private List<Program> programs;


}
