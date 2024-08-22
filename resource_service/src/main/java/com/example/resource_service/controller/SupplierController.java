package com.example.resource_service.controller;

import com.example.resource_service.model.Supplier;
import com.example.resource_service.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/supplier")
@AllArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllSuppliers() {
        List<Supplier> supplierList = supplierService.getAll();
        return ResponseEntity.ok().body(Map.of("response", supplierList,
                "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return ResponseEntity.ok().body(Map.of("response", supplier,
                "ok", true));
    }

    @PostMapping("/new")
    public ResponseEntity<?> createSupplier(@RequestBody Supplier supplier) {
        Supplier newSupplier = supplierService.create(supplier);
        return ResponseEntity.ok().body(Map.of("response", newSupplier,
                "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.update(id, supplier);
        return ResponseEntity.ok().body(Map.of("response", updatedSupplier,
                "ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        boolean isDeleted = supplierService.delete(id);
        return ResponseEntity.ok().body(Map.of("response", isDeleted,
                "ok", true));
    }
}
