package com.example.resource_service.service;

import com.example.resource_service.model.Step;
import com.example.resource_service.repository.StepRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@AllArgsConstructor
public class StepService implements ServiceTemplate<Step>{
    private final StepRepo stepRepo;
    @Override
    public Step create(Step entity) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Step update(Long id, Step entity) {
        return null;
    }

    @Override
    public Step getById(Long id) {
        return null;
    }

    @Override
    public List<Step> getAll() {
        return List.of();
    }
}
