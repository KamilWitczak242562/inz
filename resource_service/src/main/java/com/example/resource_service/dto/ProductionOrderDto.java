package com.example.resource_service.dto;

import lombok.Data;

@Data
public class ProductionOrderDto {
    private Long productionOrderId;

    private Long machineId;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
}
