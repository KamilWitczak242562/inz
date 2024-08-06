package com.example.resource_service.mapper;

import com.example.resource_service.dto.SupplierDto;
import com.example.resource_service.model.Supplier;

public class SupplierMapper {

    public SupplierDto toDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierDto dto = new SupplierDto();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setName(supplier.getName());
        dto.setContactInfo(supplier.getContactInfo());
        dto.setAddress(supplier.getAddress());

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

        return supplier;
    }
}
