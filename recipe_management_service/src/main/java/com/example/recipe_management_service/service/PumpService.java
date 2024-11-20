package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Pump;
import com.example.recipe_management_service.repository.PumpRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PumpService implements ServiceTemplate<Pump> {
    private final PumpRepo pumpRepository;

    @Override
    public List<Pump> getAll() {
        return pumpRepository.findAll();
    }

    @Override
    public Pump getById(Long id) {
        return pumpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Pump found with id: " + id));
    }

    @Override
    public Pump create(Pump pump) {
        return pumpRepository.save(pump);
    }

    @Override
    public Pump update(Long id, Pump pump) {
        Pump pumpToUpdate = pumpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Pump found with id: " + id));
        pumpToUpdate.setRpm(pump.getRpm());
        pumpToUpdate.setCirculationTimeInOut(pump.getCirculationTimeInOut());
        pumpToUpdate.setCirculationTimeOutIn(pump.getCirculationTimeOutIn());
        return pumpRepository.save(pumpToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (pumpRepository.existsById(id)) {
            pumpRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}