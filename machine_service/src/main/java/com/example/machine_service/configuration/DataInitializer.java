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
        dryer1.setName("Dryer Alpha");
        dryer1.setState(State.WORKING);
        dryer1.setStartWork(formatDate(LocalDateTime.now().minusDays(5)));
        dryer1.setCapacity(980);
        dryer1.setDryerType(DryerType.PRESSURE);
        dryerRepository.save(dryer1);

        simulateModificationForDryer(dryer1, State.IDLE, null, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3));
        simulateModificationForDryer(dryer1, State.WAITING_FOR_ACTION, 1000, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
        simulateModificationForDryer(dryer1, State.WORKING, null, LocalDateTime.now().minusDays(1), LocalDateTime.now());

        Dryer dryer2 = new Dryer();
        dryer2.setName("Dryer Beta");
        dryer2.setDryerType(DryerType.HIGH_FREQUENCY);
        dryer2.setCapacity(800);
        dryer2.setState(State.WORKING);
        dryer2.setStartWork(formatDate(LocalDateTime.now().minusDays(13)));
        dryerRepository.save(dryer2);

        simulateModificationForDryer(dryer2, State.ERROR, null, LocalDateTime.now().minusDays(13), LocalDateTime.now().minusDays(11));
        simulateModificationForDryer(dryer2, State.IDLE, 850, LocalDateTime.now().minusDays(11), LocalDateTime.now().minusDays(9));

        Dryer dryer3 = new Dryer();
        dryer3.setName("Dryer Gamma");
        dryer3.setDryerType(DryerType.PRESSURE);
        dryer3.setCapacity(1150);
        dryer3.setState(State.WORKING);
        dryer3.setStartWork(formatDate(LocalDateTime.now().minusDays(26)));
        dryerRepository.save(dryer3);

        simulateModificationForDryer(dryer3, State.WAITING_FOR_ACTION, null, LocalDateTime.now().minusDays(26), LocalDateTime.now().minusDays(20));
        simulateModificationForDryer(dryer3, State.ERROR, 1200, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(15));
        simulateModificationForDryer(dryer3, State.WAITING_FOR_ACTION, null, LocalDateTime.now().minusDays(15), LocalDateTime.now());
    }

    private void initDyeingMachines() {
        Dyeing dyeing1 = new Dyeing();
        dyeing1.setName("Dyeing Alpha");
        dyeing1.setState(State.WORKING);
        dyeing1.setStartWork(formatDate(LocalDateTime.now().minusDays(10)));
        dyeing1.setCapacity(980);
        dyeing1.setCharge_diameter(980);
        dyeingRepository.save(dyeing1);

        simulateModificationForDyeing(dyeing1, State.IDLE, null, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7));
        simulateModificationForDyeing(dyeing1, State.WAITING_FOR_ACTION, 1000, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(5));
        simulateModificationForDyeing(dyeing1, State.WORKING, null, LocalDateTime.now().minusDays(5), LocalDateTime.now());

        Dyeing dyeing2 = new Dyeing();
        dyeing2.setName("Dyeing Beta");
        dyeing2.setState(State.WORKING);
        dyeing2.setStartWork(formatDate(LocalDateTime.now().minusDays(15)));
        dyeing2.setCapacity(800);
        dyeing2.setCharge_diameter(800);
        dyeingRepository.save(dyeing2);

        simulateModificationForDyeing(dyeing2, State.ERROR, null, LocalDateTime.now().minusDays(15), LocalDateTime.now().minusDays(13));
        simulateModificationForDyeing(dyeing2, State.IDLE, 850, LocalDateTime.now().minusDays(13), LocalDateTime.now().minusDays(10));

        Dyeing dyeing3 = new Dyeing();
        dyeing3.setName("Dyeing Gamma");
        dyeing3.setState(State.WORKING);
        dyeing3.setStartWork(formatDate(LocalDateTime.now().minusDays(30)));
        dyeing3.setCapacity(1150);
        dyeing3.setCharge_diameter(1150);
        dyeingRepository.save(dyeing3);

        simulateModificationForDyeing(dyeing3, State.WAITING_FOR_ACTION, null, LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(25));
        simulateModificationForDyeing(dyeing3, State.ERROR, 1200, LocalDateTime.now().minusDays(25), LocalDateTime.now().minusDays(20));
        simulateModificationForDyeing(dyeing3, State.WAITING_FOR_ACTION, null, LocalDateTime.now().minusDays(20), LocalDateTime.now());
    }

    private void simulateModificationForDryer(Dryer dryer, State state, Integer capacity, LocalDateTime startWork, LocalDateTime endWork) {
        if (state != null) {
            dryer.setState(state);
        }
        if (capacity != null) {
            dryer.setCapacity(capacity);
        }
        if (startWork != null) {
            dryer.setStartWork(formatDate(startWork));
        }
        if (endWork != null) {
            dryer.setEndWork(formatDate(endWork));
        }
        dryerRepository.save(dryer);
    }

    private void simulateModificationForDyeing(Dyeing dyeing, State state, Integer capacity, LocalDateTime startWork, LocalDateTime endWork) {
        if (state != null) {
            dyeing.setState(state);
        }
        if (capacity != null) {
            dyeing.setCapacity(capacity);
        }
        if (startWork != null) {
            dyeing.setStartWork(formatDate(startWork));
        }
        if (endWork != null) {
            dyeing.setEndWork(formatDate(endWork));
        }
        dyeingRepository.save(dyeing);
    }

    private LocalDateTime formatDate(LocalDateTime dateTime) {
        return LocalDateTime.parse(dateTime.format(formatter), formatter);
    }
}
