package com.example.resource_service.mapper;

import com.example.resource_service.dto.ResourceDto;
import com.example.resource_service.dto.SupplierDto;
import com.example.resource_service.model.Resource;
import com.example.resource_service.model.Supplier;

import java.util.List;
import java.util.stream.Collectors;

public class SupplierMapper {

    private final ResourceMapper resourceMapper;

    public SupplierMapper() {
        this.resourceMapper = new ResourceMapper();
    }

    public SupplierDto toDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierDto dto = new SupplierDto();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setName(supplier.getName());
        dto.setContactInfo(supplier.getContactInfo());
        dto.setAddress(supplier.getAddress());
        dto.setResources(mapResourcesToDtos(supplier.getResources()));

        return dto;
    }

    public Supplier fromDto(SupplierDto dto) {
        if (dto == null) {
            return null;
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierId(dto.getSupplierId());
        supplier.setName(dto.getName());
        supplier.setContactInfo(dto.getContactInfo());
        supplier.setAddress(dto.getAddress());
        supplier.setResources(mapDtosToResources(dto.getResources()));

        return supplier;
    }

    private List<ResourceDto> mapResourcesToDtos(List<Resource> resources) {
        if (resources == null) {
            return null;
        }
        return resources.stream()
                .map(resourceMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<Resource> mapDtosToResources(List<ResourceDto> resourceDtos) {
        if (resourceDtos == null) {
            return null;
        }
        return resourceDtos.stream()
                .map(resourceMapper::fromDto)
                .collect(Collectors.toList());
    }
}
