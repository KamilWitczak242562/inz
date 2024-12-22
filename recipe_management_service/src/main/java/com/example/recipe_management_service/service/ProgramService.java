package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Program;
import com.example.recipe_management_service.repository.BlockRepo;
import com.example.recipe_management_service.repository.ProgramRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProgramService implements ServiceTemplate<Program> {
    private final ProgramRepo programRepository;
    private final BlockRepo blockRepo;

    @Override
    public List<Program> getAll() {
        return programRepository.findAll();
    }

    @Override
    public Program getById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + id));
    }

    @Override
    public Program create(Program program) {
        validateBlockIds(program.getBlockIds());
        return programRepository.save(program);
    }

    @Override
    public Program update(Long id, Program program) {
        Program programToUpdate = programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + id));
        programToUpdate.setName(program.getName());
        programToUpdate.setBlockIds(program.getBlockIds());
        validateBlockIds(program.getBlockIds());
        return programRepository.save(programToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (programRepository.existsById(id)) {
            programRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public void addBlockToProgram(Long programId, Long blockId) {
        if (!blockRepo.existsById(blockId)) {
            throw new IllegalArgumentException("No Block found with id: " + blockId);
        }
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + programId));
        program.getBlockIds().add(blockId);
        programRepository.save(program);
    }

    public void removeBlockFromProgram(Long programId, Long blockId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("No Program found with id: " + programId));
        program.getBlockIds().remove(blockId);
        programRepository.save(program);
    }

    private void validateBlockIds(List<Long> blockIds) {
        for (Long blockId : blockIds) {
            if (!blockRepo.existsById(blockId)) {
                throw new IllegalArgumentException("No Block found with id: " + blockId);
            }
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Program.class, false, true)
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
                    Program program = (Program) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "program", program,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }
}
