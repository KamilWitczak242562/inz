package com.example.recipe_management_service.configuration;

import com.example.recipe_management_service.model.MainTank;
import com.example.recipe_management_service.model.Pump;
import com.example.recipe_management_service.model.SecondaryTank;
import com.example.recipe_management_service.repository.BlockRepo;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class BlockDataInitializer implements CommandLineRunner {
    private final BlockRepo blockRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initMainTanks();
        initSecondaryTanks();
        initPumps();
    }

    private void initMainTanks() {
        MainTank mainTank1 = new MainTank();
        mainTank1.setFillLevel(50.0);
        mainTank1.setIsHotWater(true);
        mainTank1.setTargetTemperature(60.0);
        mainTank1.setTemperatureIncreaseRate(5.0);
        mainTank1.setHoldTemperatureTime(120);
        mainTank1.setIsDrainActive(false);
        blockRepository.save(mainTank1);

        simulateModificationForMainTank(mainTank1);

        MainTank mainTank2 = new MainTank();
        mainTank2.setFillLevel(30.0);
        mainTank2.setIsHotWater(false);
        mainTank2.setTargetTemperature(40.0);
        mainTank2.setTemperatureIncreaseRate(2.5);
        mainTank2.setHoldTemperatureTime(60);
        mainTank2.setIsDrainActive(true);
        blockRepository.save(mainTank2);

        simulateModificationForMainTank(mainTank2);
    }

    private void simulateModificationForMainTank(MainTank mainTank) {
        mainTank.setFillLevel(mainTank.getFillLevel() + 10.0);
        mainTank.setIsHotWater(!mainTank.getIsHotWater());
        blockRepository.save(mainTank);
    }

    private void initSecondaryTanks() {
        SecondaryTank secondaryTank1 = new SecondaryTank();
        secondaryTank1.setFillLevel(80.0);
        secondaryTank1.setIsHotWater(true);
        secondaryTank1.setTargetTemperature(70.0);
        secondaryTank1.setTemperatureIncreaseRate(4.0);
        secondaryTank1.setHoldTemperatureTime(100);
        secondaryTank1.setIsDrainActive(false);
        secondaryTank1.setIsMixerActive(true);
        secondaryTank1.setChemicalDose(5.0);
        secondaryTank1.setDyeDose(2.0);
        blockRepository.save(secondaryTank1);

        simulateModificationForSecondaryTank(secondaryTank1);

        SecondaryTank secondaryTank2 = new SecondaryTank();
        secondaryTank2.setFillLevel(60.0);
        secondaryTank2.setIsHotWater(false);
        secondaryTank2.setTargetTemperature(50.0);
        secondaryTank2.setTemperatureIncreaseRate(3.0);
        secondaryTank2.setHoldTemperatureTime(80);
        secondaryTank2.setIsDrainActive(true);
        secondaryTank2.setIsMixerActive(false);
        secondaryTank2.setChemicalDose(3.0);
        secondaryTank2.setDyeDose(1.5);
        blockRepository.save(secondaryTank2);

        simulateModificationForSecondaryTank(secondaryTank2);
    }

    private void simulateModificationForSecondaryTank(SecondaryTank secondaryTank) {
        secondaryTank.setChemicalDose(secondaryTank.getChemicalDose() + 1.0);
        secondaryTank.setIsMixerActive(!secondaryTank.getIsMixerActive());
        blockRepository.save(secondaryTank);
    }

    private void initPumps() {
        Pump pump1 = new Pump();
        pump1.setRpm(1200);
        pump1.setCirculationTimeInOut(10);
        pump1.setCirculationTimeOutIn(15);
        blockRepository.save(pump1);

        simulateModificationForPump(pump1);

        Pump pump2 = new Pump();
        pump2.setRpm(1500);
        pump2.setCirculationTimeInOut(8);
        pump2.setCirculationTimeOutIn(12);
        blockRepository.save(pump2);

        simulateModificationForPump(pump2);
    }

    private void simulateModificationForPump(Pump pump) {
        pump.setRpm(pump.getRpm() + 300);
        blockRepository.save(pump);
    }
}
