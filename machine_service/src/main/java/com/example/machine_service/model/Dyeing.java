package com.example.machine_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dyeing_machines")
@Data
public class Dyeing extends Machine{
    private int charge_diameter;
}
