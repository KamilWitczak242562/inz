package com.example.resource_service.mapper;

import com.example.resource_service.dto.ResourceDto;
import com.example.resource_service.model.Resource;

public class ResourceMapper {

    public ResourceDto toDto(Resource resource) {
        if (resource == null) {
            return null;
        }

        ResourceDto dto = new ResourceDto();
        dto.setResourceId(resource.getResourceId());
        dto.setName(resource.getName());
        dto.setDescription(resource.getDescription());
        dto.setCurrentStock(resource.getCurrentStock());
        dto.setUnit(resource.getUnit());
        dto.setReorderLevel(resource.getReorderLevel());

        return dto;
    }

    public Resource fromDto(ResourceDto dto) {
        if (dto == null) {
            return null;
        }

        Resource resource = new Resource();
        resource.setResourceId(dto.getResourceId());
        resource.setName(dto.getName());
        resource.setDescription(dto.getDescription());
        resource.setCurrentStock(dto.getCurrentStock());
        resource.setUnit(dto.getUnit());
        resource.setReorderLevel(dto.getReorderLevel());

        return resource;
    }
}
