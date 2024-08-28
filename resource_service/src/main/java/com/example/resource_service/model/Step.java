package com.example.resource_service.model;

import com.example.resource_service.model.enums.StepStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "step")
@Data
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepId;
    private String stepName;
    private String stepDescription;

    private StepStatus stepStatus = StepStatus.TODO;

}
