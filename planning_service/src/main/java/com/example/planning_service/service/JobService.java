package com.example.planning_service.service;

import com.example.planning_service.model.Job;
import com.example.planning_service.repository.JobRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class JobService implements ServiceTemplate<Job> {

    private final JobRepo jobRepo;

    @Override
    public List<Job> getAll() {
        return jobRepo.findAll();
    }

    @Override
    public Job getById(Long id) {
        return jobRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Job found with id: " + id));
    }

    @Override
    public Job create(Job job) {
        return jobRepo.save(job);
    }

    @Override
    public Job update(Long id, Job job) {
        Job jobToUpdate = jobRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Job found with id: " + id));

        jobToUpdate.setStartTime(job.getStartTime());
        jobToUpdate.setEndTime(job.getEndTime());
        jobToUpdate.setMachineId(job.getMachineId());
        jobToUpdate.setProgramId(job.getProgramId());
        jobToUpdate.setRecipeId(job.getRecipeId());
        jobToUpdate.setDryer(job.isDryer());

        return jobRepo.save(jobToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (jobRepo.existsById(id)) {
            jobRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public Job assignMachine(Long jobId, Long machineId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("No Job found with id: " + jobId));
        job.setMachineId(machineId);
        return jobRepo.save(job);
    }

    public Job assignProgram(Long jobId, Long programId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("No Job found with id: " + jobId));
        job.setProgramId(programId);
        return jobRepo.save(job);
    }

    public Job assignRecipe(Long jobId, Long recipeId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("No Job found with id: " + jobId));
        job.setRecipeId(recipeId);
        return jobRepo.save(job);
    }

    public Job startJob(Long jobId, LocalDateTime startTime, LocalDateTime endTime) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("No Job found with id: " + jobId));
        job.setStartTime(startTime);
        job.setEndTime(endTime);
        return jobRepo.save(job);
    }
}
