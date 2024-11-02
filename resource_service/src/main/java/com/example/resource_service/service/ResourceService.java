package com.example.resource_service.service;

import com.example.resource_service.model.Resource;
import com.example.resource_service.model.Supplier;
import com.example.resource_service.repository.ResourceRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@AllArgsConstructor
public class ResourceService implements ServiceTemplate<Resource> {
    private final ResourceRepo resourceRepo;

    @Override
    public Resource create(Resource entity) {
        return resourceRepo.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (resourceRepo.existsById(id)) {
            resourceRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Resource update(Long id, Resource entity) {
        Resource resourceToChange = resourceRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such resource"));
        resourceToChange.setName(entity.getName());
        resourceToChange.setDescription(entity.getDescription());
        resourceToChange.setUnit(entity.getUnit());
        resourceToChange.setReorderLevel(entity.getReorderLevel());
        resourceToChange.setCurrentStock(entity.getCurrentStock());
        return resourceRepo.save(resourceToChange);
    }

    @Override
    public Resource getById(Long id) {
        return resourceRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such resource"));
    }

    @Override
    public List<Resource> getAll() {
        return resourceRepo.findAll();
    }
}
