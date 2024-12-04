package com.example.client.model.machine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class Machine {
    private Long machineId;
    private String state;
    private String name;
    private LocalDateTime startWork;
    private LocalDateTime endWork;
    private int capacity;

}

