package com.example.machine_service.configuration;

import com.example.machine_service.model.Dryer;
import com.example.machine_service.model.DryerType;
import com.example.machine_service.model.Dyeing;
import com.example.machine_service.model.State;
import com.example.machine_service.repository.DryerRepository;
import com.example.machine_service.repository.DyeingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DryerRepository dryerRepository;
    private final DyeingRepository dyeingRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void run(String... args) {
        initDryers();
        initDyeingMachines();
    }

    @Transactional
    public void initDryers() {
        Dryer dryer1 = new Dryer();
        dryer1.setName("Dryer Alpha");
        dryer1.setState(State.WORKING);
        dryer1.setStartWork(formatDate(LocalDateTime.now().minusDays(5)));
        dryer1.setCapacity(980);
        dryer1.setDryerType(DryerType.PRESSURE);
        dryerRepository.save(dryer1);
        dryerRepository.flush();
        performDryerUpdates(dryer1, State.IDLE, null, State.WAITING_FOR_ACTION, 1000);

        Dryer dryer2 = new Dryer();
        dryer2.setName("Dryer Beta");
        dryer2.setDryerType(DryerType.HIGH_FREQUENCY);
        dryer2.setCapacity(800);
        dryer2.setState(State.WORKING);
        dryer2.setStartWork(formatDate(LocalDateTime.now().minusDays(13)));
        dryerRepository.save(dryer2);
        dryerRepository.flush();
        performDryerUpdates(dryer2, State.ERROR, 850, State.IDLE, null);

        Dryer dryer3 = new Dryer();
        dryer3.setName("Dryer Gamma");
        dryer3.setDryerType(DryerType.PRESSURE);
        dryer3.setCapacity(1150);
        dryer3.setState(State.WORKING);
        dryer3.setStartWork(formatDate(LocalDateTime.now().minusDays(26)));
        dryerRepository.save(dryer3);
        dryerRepository.flush();
        performDryerUpdates(dryer3, State.WAITING_FOR_ACTION, 1200, State.ERROR, null);
    }

    @Transactional
    public void performDryerUpdates(Dryer dryer, State firstState, Integer firstCapacity, State secondState, Integer secondCapacity) {
        simulateModificationForDryer(dryer, firstState, firstCapacity, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3));
        simulateModificationForDryer(dryer, secondState, secondCapacity, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
        simulateModificationForDryer(dryer, State.WORKING, null, LocalDateTime.now().minusDays(1), LocalDateTime.now());
    }

    @Transactional
    public void initDyeingMachines() {
        Dyeing dyeing1 = new Dyeing();
        dyeing1.setName("Dyeing Alpha");
        dyeing1.setState(State.WORKING);
        dyeing1.setStartWork(formatDate(LocalDateTime.now().minusDays(10)));
        dyeing1.setCapacity(980);
        dyeing1.setCharge_diameter(980);
        dyeingRepository.save(dyeing1);
        dyeingRepository.flush();
        performDyeingUpdates(dyeing1, State.IDLE, 1000, State.WAITING_FOR_ACTION, null);

        Dyeing dyeing2 = new Dyeing();
        dyeing2.setName("Dyeing Beta");
        dyeing2.setState(State.WORKING);
        dyeing2.setStartWork(formatDate(LocalDateTime.now().minusDays(15)));
        dyeing2.setCapacity(800);
        dyeing2.setCharge_diameter(800);
        dyeingRepository.save(dyeing2);
        dyeingRepository.flush();
        performDyeingUpdates(dyeing2, State.ERROR, null, State.IDLE, 850);

        Dyeing dyeing3 = new Dyeing();
        dyeing3.setName("Dyeing Gamma");
        dyeing3.setState(State.WORKING);
        dyeing3.setStartWork(formatDate(LocalDateTime.now().minusDays(30)));
        dyeing3.setCapacity(1150);
        dyeing3.setCharge_diameter(1150);
        dyeingRepository.save(dyeing3);
        dyeingRepository.flush();
        performDyeingUpdates(dyeing3, State.WAITING_FOR_ACTION, 1200, State.ERROR, null);
    }

    @Transactional
    public void performDyeingUpdates(Dyeing dyeing, State firstState, Integer firstCapacity, State secondState, Integer secondCapacity) {
        simulateModificationForDyeing(dyeing, firstState, firstCapacity, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7));
        simulateModificationForDyeing(dyeing, secondState, secondCapacity, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(5));
        simulateModificationForDyeing(dyeing, State.WORKING, null, LocalDateTime.now().minusDays(5), LocalDateTime.now());
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
