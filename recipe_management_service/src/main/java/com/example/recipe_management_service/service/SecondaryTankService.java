package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.SecondaryTank;
import com.example.recipe_management_service.repository.SecondaryTankRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SecondaryTankService implements ServiceTemplate<SecondaryTank> {
    private final SecondaryTankRepo secondaryTankRepository;

    @Override
    public List<SecondaryTank> getAll() {
        return secondaryTankRepository.findAll();
    }

    @Override
    public SecondaryTank getById(Long id) {
        return secondaryTankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No SecondaryTank found with id: " + id));
    }

    @Override
    public SecondaryTank create(SecondaryTank secondaryTank) {
        return secondaryTankRepository.save(secondaryTank);
    }

    @Override
    public SecondaryTank update(Long id, SecondaryTank secondaryTank) {
        SecondaryTank secondaryTankToUpdate = secondaryTankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No SecondaryTank found with id: " + id));
        secondaryTankToUpdate.setFillLevel(secondaryTank.getFillLevel());
        secondaryTankToUpdate.setIsHotWater(secondaryTank.getIsHotWater());
        secondaryTankToUpdate.setTargetTemperature(secondaryTank.getTargetTemperature());
        secondaryTankToUpdate.setTemperatureIncreaseRate(secondaryTank.getTemperatureIncreaseRate());
        secondaryTankToUpdate.setHoldTemperatureTime(secondaryTank.getHoldTemperatureTime());
        secondaryTankToUpdate.setIsDrainActive(secondaryTank.getIsDrainActive());
        secondaryTankToUpdate.setIsMixerActive(secondaryTank.getIsMixerActive());
        secondaryTankToUpdate.setChemicalDose(secondaryTank.getChemicalDose());
        secondaryTankToUpdate.setDyeDose(secondaryTank.getDyeDose());
        return secondaryTankRepository.save(secondaryTankToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (secondaryTankRepository.existsById(id)) {
            secondaryTankRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}