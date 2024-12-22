package com.example.machine_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "dyeing_machines")
@Data
@Audited
public class Dyeing extends Machine {
    private int charge_diameter;
}
