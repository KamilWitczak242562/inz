package com.example.recipe_management_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "blocks")
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
public abstract class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @ManyToMany(mappedBy = "blocks")
    @JsonIgnore
    private List<Program> programs;

    @Column(insertable = false, updatable = false)
    private String dtype;

    public abstract void updatePropertiesFrom(Block block);
}
