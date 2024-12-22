package com.example.machine_service.service;

import com.example.machine_service.model.Dyeing;
import com.example.machine_service.repository.DyeingRepository;
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
import java.util.List;

@AllArgsConstructor
@Service
public class DyeingService implements ServiceTemplate<Dyeing> {
    private final DyeingRepository dyeingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Dyeing create(Dyeing entity) {
        return dyeingRepository.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (dyeingRepository.existsById(id)) {
            dyeingRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Dyeing update(Long id, Dyeing entity) {
        Dyeing dyeingToUpdate = dyeingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dyeing machine"));
        dyeingToUpdate.setName(entity.getName());
        dyeingToUpdate.setCharge_diameter(entity.getCharge_diameter());
        dyeingToUpdate.setCapacity(entity.getCapacity());
        dyeingToUpdate.setState(entity.getState());
        dyeingToUpdate.setStartWork(entity.getStartWork());
        dyeingToUpdate.setEndWork(entity.getEndWork());
        return dyeingRepository.save(dyeingToUpdate);
    }

    @Override
    public Dyeing getById(Long id) {
        return dyeingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such dyeing entry"));
    }

    @Override
    public List<Dyeing> getAll() {
        return dyeingRepository.findAll();
    }

    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Dyeing.class, false, true)
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
                    Dyeing dyeing = (Dyeing) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "dyeing", dyeing,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }

}
