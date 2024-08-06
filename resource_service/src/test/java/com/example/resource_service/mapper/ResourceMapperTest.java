package com.example.resource_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.resource_service.dto.ResourceDto;
import com.example.resource_service.model.Resource;
import org.junit.jupiter.api.Test;

class ResourceMapperTest {
    private final ResourceMapper resourceMapper = new ResourceMapper();

    @Test
    void shouldConvertResourceToDto() {
        // Given
        Resource resource = new Resource();
        resource.setResourceId(1L);
        resource.setName("Tomato");
        resource.setDescription("Fresh tomatoes");
        resource.setCurrentStock(100.0);
        resource.setUnit("kg");
        resource.setReorderLevel(10.0);

        // When
        ResourceDto dto = resourceMapper.toDto(resource);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getResourceId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Tomato");
        assertThat(dto.getDescription()).isEqualTo("Fresh tomatoes");
        assertThat(dto.getCurrentStock()).isEqualTo(100.0);
        assertThat(dto.getUnit()).isEqualTo("kg");
        assertThat(dto.getReorderLevel()).isEqualTo(10.0);
    }

    @Test
    void shouldConvertDtoToResource() {
        // Given
        ResourceDto dto = new ResourceDto();
        dto.setResourceId(1L);
        dto.setName("Tomato");
        dto.setDescription("Fresh tomatoes");
        dto.setCurrentStock(100.0);
        dto.setUnit("kg");
        dto.setReorderLevel(10.0);

        // When
        Resource resource = resourceMapper.fromDto(dto);

        // Then
        assertThat(resource).isNotNull();
        assertThat(resource.getResourceId()).isEqualTo(1L);
        assertThat(resource.getName()).isEqualTo("Tomato");
        assertThat(resource.getDescription()).isEqualTo("Fresh tomatoes");
        assertThat(resource.getCurrentStock()).isEqualTo(100.0);
        assertThat(resource.getUnit()).isEqualTo("kg");
        assertThat(resource.getReorderLevel()).isEqualTo(10.0);
    }
}