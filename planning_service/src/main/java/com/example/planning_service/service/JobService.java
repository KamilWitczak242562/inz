package com.example.planning_service.service;

import com.example.planning_service.model.Job;
import com.example.planning_service.repository.JobRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Job.class, false, true)
                .add(AuditEntity.revisionProperty("timestamp").ge(startDate.getTime()))
                .add(AuditEntity.revisionProperty("timestamp").le(endDate.getTime()));

        if (!"ALL".equalsIgnoreCase(revisionType)) {
            RevisionType revType = switch (revisionType.toUpperCase()) {
                case "INSERT" -> RevisionType.ADD;
                case "UPDATE" -> RevisionType.MOD;
                case "DELETE" -> RevisionType.DEL;
                default -> throw new IllegalArgumentException("Invalid revision type: " + revisionType);
            };
            query.add(AuditEntity.revisionType().eq(revType));
        }

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> {
                    Job job = (Job) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "job", job,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }

}
