package com.example.client.model.machine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Dyeing extends Machine {
    private int chargeDiameter;

    public Dyeing() {
    }

    public Dyeing(Long machineId, String state, String name, LocalDateTime startWork, LocalDateTime endWork, int capacity, int chargeDiameter) {
        super(machineId, state, name, startWork, endWork, capacity);
        this.chargeDiameter = chargeDiameter;
    }
}

