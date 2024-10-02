package com.example.machine_service.service;

import com.example.machine_service.model.Dryer;
import com.example.machine_service.repository.DryerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DryerService implements ServiceTemplate<Dryer> {
    private DryerRepository dryerRepository;

    @Override
    public Dryer create(Dryer entity) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Dryer update(Long id, Dryer entity) {
        return null;
    }

    @Override
    public Dryer getById(Long id) {
        return null;
    }

    @Override
    public List<Dryer> getAll() {
        return List.of();
    }
}
