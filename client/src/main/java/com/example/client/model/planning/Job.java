package com.example.client.model.planning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {

    private Long jobId;

    private boolean isDryer;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long machineId;

    private Long programId;

    private Long recipeId;
}
