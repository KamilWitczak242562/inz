package com.example.recipe_management_service.service;

import com.example.recipe_management_service.model.Block;
import com.example.recipe_management_service.repository.BlockRepo;
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
public class BlockService implements ServiceTemplate<Block> {
    private final BlockRepo blockRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Block> getAll() {
        return blockRepository.findAll();
    }

    @Override
    public Block getById(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Block found with id: " + id));
    }

    @Override
    public Block create(Block block) {
        return blockRepository.save(block);
    }

    @Override
    public Block update(Long id, Block block) {
        Block blockToUpdate = blockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Block found with id: " + id));

        blockToUpdate.updatePropertiesFrom(block);
        return blockRepository.save(blockToUpdate);
    }

    @Override
    public boolean delete(Long id) {
        if (blockRepository.existsById(id)) {
            blockRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Block.class, false, true)
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
                    Block block = (Block) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "block", block,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }
}
