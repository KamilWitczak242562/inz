package com.example.resource_service.controller;

import com.example.resource_service.model.Supplier;
import com.example.resource_service.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
        if (supplierList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No suppliers found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", supplierList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        try {
            Supplier supplier = supplierService.getById(id);
            return ResponseEntity.ok(Map.of("response", supplier, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createSupplier(@RequestBody Supplier supplier) {
        if (supplier == null || supplier.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid supplier data", "ok", false));
        }
        Supplier newSupplier = supplierService.create(supplier);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newSupplier, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        try {
            if (supplier == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid supplier data", "ok", false));
            }
            Supplier updatedSupplier = supplierService.update(id, supplier);
            return ResponseEntity.ok(Map.of("response", updatedSupplier, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        boolean isDeleted = supplierService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "Supplier deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "Supplier not found", "ok", false));
        }
    }


    @GetMapping("/history/{startTime}&{endTime}")
    public ResponseEntity<?> getResourceHistory(@PathVariable LocalDateTime startTime, @PathVariable LocalDateTime endTime, @RequestParam(defaultValue = "ALL") String revisionType) {
        try {
            List<Map<String, Object>> history = supplierService.getHistory(startTime, endTime, revisionType);
            if (history.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("response", "No history found", "ok", false));
            }
            return ResponseEntity.ok(Map.of("response", history, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("response", e.getMessage(), "ok", false));
        }
    }
}
