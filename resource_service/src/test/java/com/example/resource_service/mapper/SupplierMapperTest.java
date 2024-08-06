package com.example.resource_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.resource_service.dto.ResourceDto;
import com.example.resource_service.dto.SupplierDto;
import com.example.resource_service.model.Resource;
import com.example.resource_service.model.Supplier;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class SupplierMapperTest {
    private final SupplierMapper supplierMapper = new SupplierMapper();

    @Test
    void shouldConvertSupplierToDto() {
        // Given
        Resource resource = new Resource();
        resource.setResourceId(1L);
        resource.setName("Resource A");
        resource.setDescription("Description A");
        resource.setCurrentStock(100.0);
        resource.setUnit("Unit A");
        resource.setReorderLevel(10.0);

        Supplier supplier = new Supplier();
        supplier.setSupplierId(1L);
        supplier.setName("ABC Supplies");
        supplier.setContactInfo("contact@abc.com");
        supplier.setAddress("5678 Oak Street");
        supplier.setResources(Collections.singletonList(resource));

        // When
        SupplierDto dto = supplierMapper.toDto(supplier);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getSupplierId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("ABC Supplies");
        assertThat(dto.getContactInfo()).isEqualTo("contact@abc.com");
        assertThat(dto.getAddress()).isEqualTo("5678 Oak Street");
        assertThat(dto.getResources()).hasSize(1);

        ResourceDto resourceDto = dto.getResources().get(0);
        assertThat(resourceDto.getResourceId()).isEqualTo(1L);
        assertThat(resourceDto.getName()).isEqualTo("Resource A");
        assertThat(resourceDto.getDescription()).isEqualTo("Description A");
        assertThat(resourceDto.getCurrentStock()).isEqualTo(100);
        assertThat(resourceDto.getUnit()).isEqualTo("Unit A");
        assertThat(resourceDto.getReorderLevel()).isEqualTo(10);
    }

    @Test
    void shouldConvertDtoToSupplier() {
        // Given
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setResourceId(1L);
        resourceDto.setName("Resource A");
        resourceDto.setDescription("Description A");
        resourceDto.setCurrentStock(100.0);
        resourceDto.setUnit("Unit A");
        resourceDto.setReorderLevel(10.0);

        SupplierDto dto = new SupplierDto();
        dto.setSupplierId(1L);
        dto.setName("ABC Supplies");
        dto.setContactInfo("contact@abc.com");
        dto.setAddress("5678 Oak Street");
        dto.setResources(Collections.singletonList(resourceDto));

        // When
        Supplier supplier = supplierMapper.fromDto(dto);

        // Then
        assertThat(supplier).isNotNull();
        assertThat(supplier.getSupplierId()).isEqualTo(1L);
        assertThat(supplier.getName()).isEqualTo("ABC Supplies");
        assertThat(supplier.getContactInfo()).isEqualTo("contact@abc.com");
        assertThat(supplier.getAddress()).isEqualTo("5678 Oak Street");
        assertThat(supplier.getResources()).hasSize(1);

        Resource resource = supplier.getResources().get(0);
        assertThat(resource.getResourceId()).isEqualTo(1L);
        assertThat(resource.getName()).isEqualTo("Resource A");
        assertThat(resource.getDescription()).isEqualTo("Description A");
        assertThat(resource.getCurrentStock()).isEqualTo(100);
        assertThat(resource.getUnit()).isEqualTo("Unit A");
        assertThat(resource.getReorderLevel()).isEqualTo(10);
    }
}