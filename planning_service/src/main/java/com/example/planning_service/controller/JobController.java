package com.example.planning_service.controller;

import com.example.planning_service.model.Job;
import com.example.planning_service.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/job")
@AllArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllJobs() {
        List<Job> jobList = jobService.getAll();
        if (jobList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No jobs found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", jobList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            Job job = jobService.getById(id);
            return ResponseEntity.ok(Map.of("response", job, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        if (job == null || job.getMachineId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid Job data", "ok", false));
        }
        Job newJob = jobService.create(job);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newJob, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job job) {
        try {
            if (job == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid Job data", "ok", false));
            }
            Job updatedJob = jobService.update(id, job);
            return ResponseEntity.ok(Map.of("response", updatedJob, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        boolean isDeleted = jobService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "Job deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "Job not found", "ok", false));
        }
    }

    @PostMapping("/{jobId}/assignMachine/{machineId}")
    public ResponseEntity<?> assignMachine(@PathVariable Long jobId, @PathVariable Long machineId) {
        try {
            Job updatedJob = jobService.assignMachine(jobId, machineId);
            return ResponseEntity.ok(Map.of("response", updatedJob, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/{jobId}/assignProgram/{programId}")
    public ResponseEntity<?> assignProgram(@PathVariable Long jobId, @PathVariable Long programId) {
        try {
            Job updatedJob = jobService.assignProgram(jobId, programId);
            return ResponseEntity.ok(Map.of("response", updatedJob, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/{jobId}/assignRecipe/{recipeId}")
    public ResponseEntity<?> assignRecipe(@PathVariable Long jobId, @PathVariable Long recipeId) {
        try {
            Job updatedJob = jobService.assignRecipe(jobId, recipeId);
            return ResponseEntity.ok(Map.of("response", updatedJob, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/{jobId}/startJob")
    public ResponseEntity<?> startJob(@PathVariable Long jobId,
                                      @RequestParam LocalDateTime startTime,
                                      @RequestParam LocalDateTime endTime) {
        try {
            Job startedJob = jobService.startJob(jobId, startTime, endTime);
            return ResponseEntity.ok(Map.of("response", startedJob, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }
}
