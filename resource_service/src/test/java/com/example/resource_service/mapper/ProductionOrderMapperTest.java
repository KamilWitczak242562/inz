package com.example.resource_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.resource_service.dto.ProductionOrderDto;
import com.example.resource_service.model.ProductionOrder;
import com.example.resource_service.model.enums.ProductionStatus;
import org.junit.jupiter.api.Test;

public class ProductionOrderMapperTest {

    private final ProductionOrderMapper mapper = new ProductionOrderMapper();

    @Test
    void shouldConvertProductionOrderToDto() {
        // Given
        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setProductionOrderId(1L);
        productionOrder.setMachineId(101L);
        productionOrder.setDescription("Order Description");
        productionOrder.setStartDate("2024-01-01");
        productionOrder.setEndDate("2024-01-10");
        productionOrder.setStatus(ProductionStatus.COMPLETED);

        // When
        ProductionOrderDto dto = mapper.toDto(productionOrder);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getProductionOrderId()).isEqualTo(1L);
        assertThat(dto.getMachineId()).isEqualTo(101L);
        assertThat(dto.getDescription()).isEqualTo("Order Description");
        assertThat(dto.getStartDate()).isEqualTo("2024-01-01");
        assertThat(dto.getEndDate()).isEqualTo("2024-01-10");
        assertThat(dto.getStatus()).isEqualTo(ProductionStatus.COMPLETED);
    }

    @Test
    void shouldConvertDtoToProductionOrder() {
        // Given
        ProductionOrderDto dto = new ProductionOrderDto();
        dto.setProductionOrderId(1L);
        dto.setMachineId(101L);
        dto.setDescription("Order Description");
        dto.setStartDate("2024-01-01");
        dto.setEndDate("2024-01-10");
        dto.setStatus(ProductionStatus.COMPLETED);

        // When
        ProductionOrder productionOrder = mapper.fromDto(dto);

        // Then
        assertThat(productionOrder).isNotNull();
        assertThat(productionOrder.getProductionOrderId()).isEqualTo(1L);
        assertThat(productionOrder.getMachineId()).isEqualTo(101L);
        assertThat(productionOrder.getDescription()).isEqualTo("Order Description");
        assertThat(productionOrder.getStartDate()).isEqualTo("2024-01-01");
        assertThat(productionOrder.getEndDate()).isEqualTo("2024-01-10");
        assertThat(productionOrder.getStatus()).isEqualTo(ProductionStatus.COMPLETED);
    }
}
