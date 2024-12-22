package com.example.recipe_management_service.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "blocks")
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dtype"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MainTank.class, name = "MainTank"),
        @JsonSubTypes.Type(value = SecondaryTank.class, name = "SecondaryTank"),
        @JsonSubTypes.Type(value = Pump.class, name = "Pump")
})
public abstract class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @Column(insertable = false, updatable = false)
    private String dtype;

    public abstract void updatePropertiesFrom(Block block);
}
