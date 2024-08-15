package com.example.resource_service.controller;

import com.example.resource_service.model.Supplier;
import com.example.resource_service.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supplier")
@AllArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllSuppliers() {
        List<Supplier> supplierList = supplierService.getAll();
        return ResponseEntity.ok(supplierList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return ResponseEntity.ok(supplier);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createSupplier(@RequestBody Supplier supplier) {
        Supplier newSupplier = supplierService.create(supplier);
        return ResponseEntity.ok(newSupplier);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.update(id, supplier);
        return ResponseEntity.ok(updatedSupplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        boolean isDeleted = supplierService.delete(id);
        return ResponseEntity.ok(isDeleted);
    }
}
