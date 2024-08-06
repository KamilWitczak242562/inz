package com.example.resource_service.model;

import com.example.resource_service.model.enums.ProductionStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "production_orders")
@Data
public class ProductionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productionOrderId;

    private Long machineId;
    private String description;
    private String startDate;
    private String endDate;
    @Enumerated(EnumType.STRING)
    private ProductionStatus status;
}
