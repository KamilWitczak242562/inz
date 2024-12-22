package com.example.resource_service.service;

import com.example.resource_service.model.Supplier;
import com.example.resource_service.repository.SupplierRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SupplierService implements ServiceTemplate<Supplier> {
    private final SupplierRepo supplierRepo;


    @Override
    public Supplier create(Supplier entity) {
        return supplierRepo.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (supplierRepo.existsById(id)) {
            supplierRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Supplier update(Long id, Supplier entity) {
        Supplier supplierToChange = supplierRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such supplier"));
        supplierToChange.setAddress(entity.getAddress());
        supplierToChange.setName(entity.getName());
        supplierToChange.setContactInfo(entity.getContactInfo());
        supplierToChange.setResources(entity.getResources());
        return supplierRepo.save(supplierToChange);
    }

    @Override
    public Supplier getById(Long id) {
        return supplierRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such supplier"));
    }

    @Override
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }
}
