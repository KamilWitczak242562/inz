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
        mainTank1.setFillLevel(80.0);
        mainTank1.setIsHotWater(true);
        mainTank1.setTargetTemperature(70.0);
        mainTank1.setTemperatureIncreaseRate(1.5);
        mainTank1.setHoldTemperatureTime(150);
        mainTank1.setIsDrainActive(true);
        blockRepository.save(mainTank1);

        MainTank mainTank2 = new MainTank();
        mainTank2.setFillLevel(65.0);
        mainTank2.setIsHotWater(false);
        mainTank2.setTargetTemperature(55.0);
        mainTank2.setTemperatureIncreaseRate(2.0);
        mainTank2.setHoldTemperatureTime(120);
        mainTank2.setIsDrainActive(false);
        blockRepository.save(mainTank2);

        MainTank mainTank3 = new MainTank();
        mainTank3.setFillLevel(90.0);
        mainTank3.setIsHotWater(true);
        mainTank3.setTargetTemperature(85.0);
        mainTank3.setTemperatureIncreaseRate(1.2);
        mainTank3.setHoldTemperatureTime(180);
        mainTank3.setIsDrainActive(true);
        blockRepository.save(mainTank3);
    }

    private void initSecondaryTanks() {
        SecondaryTank secondaryTank1 = new SecondaryTank();
        secondaryTank1.setFillLevel(75.0);
        secondaryTank1.setIsHotWater(true);
        secondaryTank1.setTargetTemperature(65.0);
        secondaryTank1.setTemperatureIncreaseRate(1.8);
        secondaryTank1.setHoldTemperatureTime(140);
        secondaryTank1.setIsDrainActive(false);
        secondaryTank1.setIsMixerActive(true);
        secondaryTank1.setChemicalDose(4.5);
        secondaryTank1.setDyeDose(2.2);
        blockRepository.save(secondaryTank1);

        SecondaryTank secondaryTank2 = new SecondaryTank();
        secondaryTank2.setFillLevel(50.0);
        secondaryTank2.setIsHotWater(false);
        secondaryTank2.setTargetTemperature(45.0);
        secondaryTank2.setTemperatureIncreaseRate(1.0);
        secondaryTank2.setHoldTemperatureTime(90);
        secondaryTank2.setIsDrainActive(true);
        secondaryTank2.setIsMixerActive(false);
        secondaryTank2.setChemicalDose(3.0);
        secondaryTank2.setDyeDose(1.8);
        blockRepository.save(secondaryTank2);

        SecondaryTank secondaryTank3 = new SecondaryTank();
        secondaryTank3.setFillLevel(85.0);
        secondaryTank3.setIsHotWater(true);
        secondaryTank3.setTargetTemperature(75.0);
        secondaryTank3.setTemperatureIncreaseRate(1.5);
        secondaryTank3.setHoldTemperatureTime(160);
        secondaryTank3.setIsDrainActive(false);
        secondaryTank3.setIsMixerActive(true);
        secondaryTank3.setChemicalDose(5.0);
        secondaryTank3.setDyeDose(3.0);
        blockRepository.save(secondaryTank3);
    }

    private void initPumps() {
        Pump pump1 = new Pump();
        pump1.setRpm(1000);
        pump1.setCirculationTimeInOut(20);
        pump1.setCirculationTimeOutIn(25);
        blockRepository.save(pump1);

        Pump pump2 = new Pump();
        pump2.setRpm(1400);
        pump2.setCirculationTimeInOut(15);
        pump2.setCirculationTimeOutIn(20);
        blockRepository.save(pump2);

        Pump pump3 = new Pump();
        pump3.setRpm(1600);
        pump3.setCirculationTimeInOut(10);
        pump3.setCirculationTimeOutIn(12);
        blockRepository.save(pump3);
    }
}

