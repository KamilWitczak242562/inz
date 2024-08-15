package com.example.resource_service.controller;

import com.example.resource_service.model.ProductionOrder;
import com.example.resource_service.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/productionOrder")
@AllArgsConstructor
public class ProductionOrderController {
    private final ProductionOrderService productionOrderService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllProductionOrders() {
        List<ProductionOrder> productionOrderList = productionOrderService.getAll();
        return ResponseEntity.ok(productionOrderList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductionOrderById(@PathVariable Long id) {
        ProductionOrder productionOrder = productionOrderService.getById(id);
        return ResponseEntity.ok(productionOrder);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createProductionOrder(@RequestBody ProductionOrder productionOrder) {
        ProductionOrder newProductionOrder = productionOrderService.create(productionOrder);
        return ResponseEntity.ok(newProductionOrder);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateProductionOrder(@PathVariable Long id, @RequestBody ProductionOrder productionOrder) {
        ProductionOrder updatedProductionOrder = productionOrderService.update(id, productionOrder);
        return ResponseEntity.ok(updatedProductionOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductionOrder(@PathVariable Long id) {
        boolean isDeleted = productionOrderService.delete(id);
        return ResponseEntity.ok(isDeleted);
    }
}
