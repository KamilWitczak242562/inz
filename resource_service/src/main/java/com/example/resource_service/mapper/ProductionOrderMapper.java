package com.example.resource_service.mapper;

import com.example.resource_service.dto.ProductionOrderDto;
import com.example.resource_service.model.ProductionOrder;

public class ProductionOrderMapper {

    public ProductionOrderDto toDto(ProductionOrder productionOrder) {
        if (productionOrder == null) {
            return null;
        }

        ProductionOrderDto dto = new ProductionOrderDto();
        dto.setProductionOrderId(productionOrder.getProductionOrderId());
        dto.setMachineId(productionOrder.getMachineId());
        dto.setDescription(productionOrder.getDescription());
        dto.setStartDate(productionOrder.getStartDate());
        dto.setEndDate(productionOrder.getEndDate());
        dto.setStatus(productionOrder.getStatus());

        return dto;
    }

    public ProductionOrder fromDto(ProductionOrderDto dto) {
        if (dto == null) {
            return null;
        }

        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setProductionOrderId(dto.getProductionOrderId());
        productionOrder.setMachineId(dto.getMachineId());
        productionOrder.setDescription(dto.getDescription());
        productionOrder.setStartDate(dto.getStartDate());
        productionOrder.setEndDate(dto.getEndDate());
        productionOrder.setStatus(dto.getStatus());

        return productionOrder;
    }
}
