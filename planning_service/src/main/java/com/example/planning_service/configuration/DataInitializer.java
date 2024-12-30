package com.example.planning_service.configuration;

import com.example.planning_service.model.Job;
import com.example.planning_service.repository.JobRepo;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final JobRepo jobRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    @Transactional
    public void run(String... args) {
        initJobs();
    }

    private void initJobs() {
        Job job1 = new Job();
        job1.setDryer(true);
        job1.setStartTime(formatDate(LocalDateTime.now().minusDays(10)));
        job1.setEndTime(formatDate(LocalDateTime.now().minusDays(9)));
        job1.setMachineId(1L);
        job1.setProgramId(1L);
        job1.setRecipeId(1L);
        jobRepository.save(job1);

        simulateModificationForJob(job1);

        Job job2 = new Job();
        job2.setDryer(false);
        job2.setStartTime(formatDate(LocalDateTime.now().minusDays(8)));
        job2.setEndTime(formatDate(LocalDateTime.now().minusDays(7)));
        job2.setMachineId(2L);
        job2.setProgramId(2L);
        job2.setRecipeId(2L);
        jobRepository.save(job2);

        simulateModificationForJob(job2);
    }

    private void simulateModificationForJob(Job job) {
        job.setEndTime(formatDate(LocalDateTime.now()));
        job.setRecipeId(job.getRecipeId() + 1);
        jobRepository.save(job);
    }

    private LocalDateTime formatDate(LocalDateTime dateTime) {
        return LocalDateTime.parse(dateTime.format(formatter), formatter);
    }
}
