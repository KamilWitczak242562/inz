package com.example.resource_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.resource_service.dto.SupplierDto;
import com.example.resource_service.model.Supplier;
import org.junit.jupiter.api.Test;

class SupplierMapperTest {
    private final SupplierMapper supplierMapper = new SupplierMapper();

    @Test
    void shouldConvertSupplierToDto() {
        // Given
        Supplier supplier = new Supplier();
        supplier.setSupplierId(1L);
        supplier.setName("ABC Supplies");
        supplier.setContactInfo("contact@abc.com");
        supplier.setAddress("5678 Oak Street");

        // When
        SupplierDto dto = supplierMapper.toDto(supplier);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getSupplierId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("ABC Supplies");
        assertThat(dto.getContactInfo()).isEqualTo("contact@abc.com");
        assertThat(dto.getAddress()).isEqualTo("5678 Oak Street");
    }

    @Test
    void shouldConvertDtoToSupplier() {
        // Given
        SupplierDto dto = new SupplierDto();
        dto.setSupplierId(1L);
        dto.setName("ABC Supplies");
        dto.setContactInfo("contact@abc.com");
        dto.setAddress("5678 Oak Street");

        // When
        Supplier supplier = supplierMapper.fromDto(dto);

        // Then
        assertThat(supplier).isNotNull();
        assertThat(supplier.getSupplierId()).isEqualTo(1L);
        assertThat(supplier.getName()).isEqualTo("ABC Supplies");
        assertThat(supplier.getContactInfo()).isEqualTo("contact@abc.com");
        assertThat(supplier.getAddress()).isEqualTo("5678 Oak Street");
    }
}