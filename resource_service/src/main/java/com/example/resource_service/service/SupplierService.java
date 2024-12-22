package com.example.resource_service.service;

import com.example.resource_service.model.Resource;
import com.example.resource_service.model.Supplier;
import com.example.resource_service.repository.SupplierRepo;
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
public class SupplierService implements ServiceTemplate<Supplier> {
    private final SupplierRepo supplierRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Supplier create(Supplier entity) {
        return supplierRepo.save(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (supplierRepo.existsById(id)) {
            supplierRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Supplier update(Long id, Supplier entity) {
        Supplier supplierToChange = supplierRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such supplier"));
        supplierToChange.setAddress(entity.getAddress());
        supplierToChange.setName(entity.getName());
        supplierToChange.setContactInfo(entity.getContactInfo());
        supplierToChange.setResources(entity.getResources());
        return supplierRepo.save(supplierToChange);
    }

    @Override
    public Supplier getById(Long id) {
        return supplierRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such supplier"));
    }

    @Override
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }

    @Override
    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Supplier.class, false, true)
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
                    Supplier supplier = (Supplier) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "supplier", supplier,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }
}
