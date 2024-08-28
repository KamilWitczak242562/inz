package com.example.resource_service.service;

import com.example.resource_service.model.Program;
import com.example.resource_service.repository.ProgramRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProgramService implements ServiceTemplate<Program>{
    private final ProgramRepo programRepo;
    @Override
    public Program create(Program entity) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Program update(Long id, Program entity) {
        return null;
    }

    @Override
    public Program getById(Long id) {
        return null;
    }

    @Override
    public List<Program> getAll() {
        return List.of();
    }
}
