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
    public void run(String... args) {
        initJobs();
    }

    @Transactional
    public void initJobs() {
        Job job1 = new Job();
        job1.setDryer(true);
        job1.setStartTime(formatDate(LocalDateTime.now().minusDays(15)));
        job1.setEndTime(formatDate(LocalDateTime.now().minusDays(14).plusHours(2).plusMinutes(30)));
        job1.setMachineId(1L);
        job1.setProgramId(1L);
        job1.setRecipeId(1L);
        jobRepository.save(job1);

        jobRepository.flush();
        performJobUpdates(job1, 2L, formatDate(LocalDateTime.now().minusDays(14).plusHours(3)));

        Job job2 = new Job();
        job2.setDryer(false);
        job2.setStartTime(formatDate(LocalDateTime.now().minusDays(13)));
        job2.setEndTime(formatDate(LocalDateTime.now().minusDays(12).plusHours(1).plusMinutes(45)));
        job2.setMachineId(2L);
        job2.setProgramId(2L);
        job2.setRecipeId(2L);
        jobRepository.save(job2);

        jobRepository.flush();
        performJobUpdates(job2, 3L, formatDate(LocalDateTime.now().minusDays(12).plusHours(2)));

        Job job3 = new Job();
        job3.setDryer(true);
        job3.setStartTime(formatDate(LocalDateTime.now().minusDays(11).plusMinutes(15)));
        job3.setEndTime(formatDate(LocalDateTime.now().minusDays(10).plusHours(2).plusMinutes(45)));
        job3.setMachineId(3L);
        job3.setProgramId(3L);
        job3.setRecipeId(3L);
        jobRepository.save(job3);

        jobRepository.flush();
        performJobUpdates(job3, 2L, formatDate(LocalDateTime.now().minusDays(10).plusHours(3)));
    }

    @Transactional
    public void performJobUpdates(Job job, Long newRecipeId, LocalDateTime newEndTime) {
        simulateModificationForJob(job, newRecipeId, newEndTime);
    }

    private void simulateModificationForJob(Job job, Long newRecipeId, LocalDateTime newEndTime) {
        job.setEndTime(newEndTime);
        job.setRecipeId(newRecipeId);
        jobRepository.save(job);
    }

    private LocalDateTime formatDate(LocalDateTime dateTime) {
        return LocalDateTime.parse(dateTime.format(formatter), formatter);
    }
}
