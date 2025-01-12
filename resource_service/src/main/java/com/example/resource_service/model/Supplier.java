package com.example.resource_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Data
@Audited
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    private String name;
    private String contactInfo;
    private String address;

    @OneToMany
    @JoinColumn(name = "supplier_id")
    private List<Resource> resources = new ArrayList<>();
}