package com.example.machine_service.service;

import com.example.machine_service.model.Dryer;
import com.example.machine_service.repository.DryerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DryerService implements ServiceTemplate<Dryer> {
    private final DryerRepository dryerRepository;

    @Override
    public Dryer create(Dryer entity) {
        return dryerRepository.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (dryerRepository.existsById(id)) {
            dryerRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Dryer update(Long id, Dryer entity) {
        Dryer dryerToChange = dryerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dryer"));
        dryerToChange.setName(entity.getName());
        dryerToChange.setDryerType(entity.getDryerType());
        dryerToChange.setCapacity(entity.getCapacity());
        dryerToChange.setState(entity.getState());
        dryerToChange.setStartWork(entity.getStartWork());
        dryerToChange.setEndWork(entity.getEndWork());
        return dryerRepository.save(dryerToChange);
    }

    @Override
    public Dryer getById(Long id) {
        return dryerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dryer"));
    }

    @Override
    public List<Dryer> getAll() {
        return dryerRepository.findAll();
    }
}
