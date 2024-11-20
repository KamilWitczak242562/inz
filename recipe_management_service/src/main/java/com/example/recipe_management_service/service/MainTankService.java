package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.MainTank;
import com.example.recipe_management_service.repository.MainTankRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MainTankService implements ServiceTemplate<MainTank> {
    private final MainTankRepo mainTankRepository;

    @Override
    public List<MainTank> getAll() {
        return mainTankRepository.findAll();
    }

    @Override
    public MainTank getById(Long id) {
        return mainTankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No MainTank found with id: " + id));
    }

    @Override
    public MainTank create(MainTank mainTank) {
        return mainTankRepository.save(mainTank);
    }

    @Override
    public MainTank update(Long id, MainTank mainTank) {
        MainTank mainTankToUpdate = mainTankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No MainTank found with id: " + id));
        mainTankToUpdate.setFillLevel(mainTank.getFillLevel());
        mainTankToUpdate.setIsHotWater(mainTank.getIsHotWater());
        mainTankToUpdate.setTargetTemperature(mainTank.getTargetTemperature());
        mainTankToUpdate.setTemperatureIncreaseRate(mainTank.getTemperatureIncreaseRate());
        mainTankToUpdate.setHoldTemperatureTime(mainTank.getHoldTemperatureTime());
        mainTankToUpdate.setIsDrainActive(mainTank.getIsDrainActive());
        return mainTankRepository.save(mainTankToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (mainTankRepository.existsById(id)) {
            mainTankRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}