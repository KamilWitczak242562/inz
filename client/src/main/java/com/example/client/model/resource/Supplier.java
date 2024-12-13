package com.example.client.model.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {

    private Long supplierId;

    private String name;
    private String contactInfo;
    private String address;

    private List<Resource> resources;

    public Supplier(String name, String contactInfo, String address, List<Resource> resources) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
        this.resources = resources;
    }
}
