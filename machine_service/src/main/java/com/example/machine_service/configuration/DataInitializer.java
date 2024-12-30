package com.example.machine_service.configuration;

import com.example.machine_service.model.Dryer;
import com.example.machine_service.model.DryerType;
import com.example.machine_service.model.Dyeing;
import com.example.machine_service.model.State;
import com.example.machine_service.repository.DryerRepository;
import com.example.machine_service.repository.DyeingRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DryerRepository dryerRepository;
    private final DyeingRepository dyeingRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    @Transactional
    public void run(String... args) {
        initDryers();
        initDyeingMachines();
    }

    private void initDryers() {
        Dryer dryer1 = new Dryer();
        dryer1.setName("Dryer 4");
        dryer1.setState(State.IDLE);
        dryer1.setStartWork(formatDate(LocalDateTime.now().minusDays(5)));
        dryer1.setEndWork(formatDate(LocalDateTime.now().minusDays(4)));
        dryer1.setCapacity(100);
        dryer1.setDryerType(DryerType.PRESSURE);
        dryerRepository.save(dryer1);

        simulateModificationForDryer(dryer1);

        Dryer dryer2 = new Dryer();
        dryer2.setName("Dryer 5");
        dryer2.setState(State.WORKING);
        dryer2.setStartWork(formatDate(LocalDateTime.now().minusDays(3)));
        dryer2.setEndWork(formatDate(LocalDateTime.now().minusDays(2)));
        dryer2.setCapacity(150);
        dryer2.setDryerType(DryerType.HIGH_FREQUENCY);
        dryerRepository.save(dryer2);

        simulateModificationForDryer(dryer2);
    }

    private void initDyeingMachines() {
        Dyeing dyeing1 = new Dyeing();
        dyeing1.setName("Dyeing 5");
        dyeing1.setState(State.WAITING_FOR_ACTION);
        dyeing1.setStartWork(formatDate(LocalDateTime.now().minusDays(7)));
        dyeing1.setEndWork(formatDate(LocalDateTime.now().minusDays(6)));
        dyeing1.setCapacity(200);
        dyeing1.setCharge_diameter(50);
        dyeingRepository.save(dyeing1);

        simulateModificationForDyeing(dyeing1);

        Dyeing dyeing2 = new Dyeing();
        dyeing2.setName("Dyeing 5");
        dyeing2.setState(State.ERROR);
        dyeing2.setStartWork(formatDate(LocalDateTime.now().minusDays(8)));
        dyeing2.setEndWork(formatDate(LocalDateTime.now().minusDays(7)));
        dyeing2.setCapacity(300);
        dyeing2.setCharge_diameter(75);
        dyeingRepository.save(dyeing2);

        simulateModificationForDyeing(dyeing2);
    }

    private void simulateModificationForDryer(Dryer dryer) {
        dryer.setState(State.WORKING);
        dryer.setEndWork(formatDate(LocalDateTime.now()));
        dryer.setDryerType(DryerType.HIGH_FREQUENCY);
        dryerRepository.save(dryer);
    }

    private void simulateModificationForDyeing(Dyeing dyeing) {
        dyeing.setState(State.IDLE);
        dyeing.setEndWork(formatDate(LocalDateTime.now()));
        dyeing.setCharge_diameter(60);
        dyeingRepository.save(dyeing);
    }

    private LocalDateTime formatDate(LocalDateTime dateTime) {
        return LocalDateTime.parse(dateTime.format(formatter), formatter);
    }
}
