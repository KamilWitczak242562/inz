package com.example.resource_service.service;

import com.example.resource_service.model.Resource;
import com.example.resource_service.repository.ResourceRepo;
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
public class ResourceService implements ServiceTemplate<Resource> {
    private final ResourceRepo resourceRepo;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Resource create(Resource entity) {
        return resourceRepo.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (resourceRepo.existsById(id)) {
            resourceRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Resource update(Long id, Resource entity) {
        Resource resourceToChange = resourceRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such resource"));
        resourceToChange.setName(entity.getName());
        resourceToChange.setDescription(entity.getDescription());
        resourceToChange.setUnit(entity.getUnit());
        resourceToChange.setReorderLevel(entity.getReorderLevel());
        resourceToChange.setCurrentStock(entity.getCurrentStock());
        return resourceRepo.save(resourceToChange);
    }

    @Override
    public Resource getById(Long id) {
        return resourceRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such resource"));
    }

    @Override
    public List<Resource> getAll() {
        return resourceRepo.findAll();
    }

    @Override
    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Resource.class, false, true)
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
                    Resource resource = (Resource) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "resource", resource,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }
}
