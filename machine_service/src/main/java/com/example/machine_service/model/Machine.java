package com.example.machine_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long machineId;

    @Enumerated(EnumType.STRING)
    private State state;

    private LocalDateTime startWork;
    private LocalDateTime endWork;
    private int capacity;

}
