package com.example.machine_service.service;

import com.example.machine_service.model.Dyeing;
import com.example.machine_service.repository.DyeingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DyeingService implements ServiceTemplate<Dyeing> {
    private final DyeingRepository dyeingRepository;

    @Override
    public Dyeing create(Dyeing entity) {
        return dyeingRepository.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (dyeingRepository.existsById(id)) {
            dyeingRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Dyeing update(Long id, Dyeing entity) {
        Dyeing dyeingToUpdate = dyeingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dyeing machine"));
        dyeingToUpdate.setName(entity.getName());
        dyeingToUpdate.setCharge_diameter(entity.getCharge_diameter());
        dyeingToUpdate.setCapacity(entity.getCapacity());
        dyeingToUpdate.setState(entity.getState());
        dyeingToUpdate.setStartWork(entity.getStartWork());
        dyeingToUpdate.setEndWork(entity.getEndWork());
        return dyeingRepository.save(dyeingToUpdate);
    }

    @Override
    public Dyeing getById(Long id) {
        return dyeingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dyeing entry"));
    }

    @Override
    public List<Dyeing> getAll() {
        return dyeingRepository.findAll();
    }
}
