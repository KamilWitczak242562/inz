package com.example.machine_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@Audited
public abstract class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long machineId;

    @Enumerated(EnumType.STRING)
    private State state;

    private String name;
    private LocalDateTime startWork;
    private LocalDateTime endWork;
    private int capacity;

}
