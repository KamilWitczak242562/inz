package com.example.client.model.machine;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Dryer extends Machine {
    private String dryerType;

    public Dryer() {
    }

    public Dryer(Long machineId, String state, String name, LocalDateTime startWork, LocalDateTime endWork, int capacity, String dryerType) {
        super(machineId, state, name, startWork, endWork, capacity);
        this.dryerType = dryerType;
    }
}
