package com.example.machine_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dryers")
@Data
public class Dryer extends Machine {
    @Enumerated(EnumType.STRING)
    private DryerType dryerType;
}
