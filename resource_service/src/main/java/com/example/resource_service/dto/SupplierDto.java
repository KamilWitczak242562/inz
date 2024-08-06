package com.example.resource_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class SupplierDto {
    private Long supplierId;

    private String name;
    private String contactInfo;
    private String address;
    private List<ResourceDto> resources;
}
