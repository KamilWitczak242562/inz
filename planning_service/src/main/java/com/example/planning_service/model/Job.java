package com.example.planning_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    private boolean isDryer;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long machineId;

    private Long programId;

    private Long recipeId;
}

