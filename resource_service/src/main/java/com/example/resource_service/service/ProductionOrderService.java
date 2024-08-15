package com.example.resource_service.service;

import com.example.resource_service.model.ProductionOrder;
import com.example.resource_service.model.Recipe;
import com.example.resource_service.repository.ProductionOrderRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductionOrderService implements ServiceTemplate<ProductionOrder> {
    private final ProductionOrderRepo productionOrderRepo;

    @Override
    public ProductionOrder create(ProductionOrder entity) {
        return productionOrderRepo.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (productionOrderRepo.existsById(id)) {
            productionOrderRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ProductionOrder update(Long id, ProductionOrder entity) {
        ProductionOrder productionOrderToChange = productionOrderRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such production order"));
        productionOrderToChange.setDescription(entity.getDescription());
        productionOrderToChange.setStatus(entity.getStatus());
        productionOrderToChange.setEndDate(entity.getEndDate());
        productionOrderToChange.setStartDate(entity.getStartDate());
        productionOrderToChange.setMachineId(entity.getMachineId());
        return productionOrderRepo.save(productionOrderToChange);
    }

    @Override
    public ProductionOrder getById(Long id) {
        return productionOrderRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such production order"));
    }

    @Override
    public List<ProductionOrder> getAll() {
        return productionOrderRepo.findAll();
    }
}
