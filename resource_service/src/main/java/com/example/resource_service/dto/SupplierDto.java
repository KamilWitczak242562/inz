package com.example.resource_service.dto;

import lombok.Data;

@Data
public class SupplierDto {
    private Long supplierId;

    private String name;
    private String contactInfo;
    private String address;
}
