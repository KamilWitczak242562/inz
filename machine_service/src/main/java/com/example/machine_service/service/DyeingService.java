package com.example.machine_service.service;

import com.example.machine_service.model.Dyeing;
import com.example.machine_service.repository.DyeingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DyeingService implements ServiceTemplate<Dyeing> {
    private DyeingRepository dyeingRepository;

    @Override
    public Dyeing create(Dyeing entity) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Dyeing update(Long id, Dyeing entity) {
        return null;
    }

    @Override
    public Dyeing getById(Long id) {
        return null;
    }

    @Override
    public List<Dyeing> getAll() {
        return List.of();
    }
}
