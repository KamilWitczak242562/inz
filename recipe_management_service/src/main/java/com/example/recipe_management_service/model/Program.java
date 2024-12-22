package com.example.recipe_management_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.List;

@Entity
@Table(name = "programs")
@Data
@Audited
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programId;

    private String name;

    @ElementCollection
    @CollectionTable(
            name = "program_block",
            joinColumns = @JoinColumn(name = "program_id")
    )
    @Column(name = "block_id")
    private List<Long> blockIds;
}
