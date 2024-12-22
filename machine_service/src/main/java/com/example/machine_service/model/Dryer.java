package com.example.machine_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "dryers")
@Data
@Audited
public class Dryer extends Machine {
    @Enumerated(EnumType.STRING)
    private DryerType dryerType;
}
