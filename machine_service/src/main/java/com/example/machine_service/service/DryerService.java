package com.example.machine_service.service;

import com.example.machine_service.model.Dryer;
import com.example.machine_service.repository.DryerRepository;
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
public class DryerService implements ServiceTemplate<Dryer> {
    private final DryerRepository dryerRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Dryer create(Dryer entity) {
        return dryerRepository.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (dryerRepository.existsById(id)) {
            dryerRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Dryer update(Long id, Dryer entity) {
        Dryer dryerToChange = dryerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dryer"));
        dryerToChange.setName(entity.getName());
        dryerToChange.setDryerType(entity.getDryerType());
        dryerToChange.setCapacity(entity.getCapacity());
        dryerToChange.setState(entity.getState());
        dryerToChange.setStartWork(entity.getStartWork());
        dryerToChange.setEndWork(entity.getEndWork());
        return dryerRepository.save(dryerToChange);
    }

    @Override
    public Dryer getById(Long id) {
        return dryerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dryer"));
    }

    @Override
    public List<Dryer> getAll() {
        return dryerRepository.findAll();
    }

    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Dryer.class, false, true)
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
                    Dryer dryer = (Dryer) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "dryer", dryer,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }

}
