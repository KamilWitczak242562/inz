package com.example.auth_service.service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public User create(User entity) {
        entity.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt()));

        return userRepo.save(entity);
    }

    public User logInUser(String email, String password) {
        User u = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User doesn't exist."));
        if (u == null) {
            throw new IllegalArgumentException("User was not found.");
        }
        String passwordCheck = u.getPassword();
        if (BCrypt.checkpw(password, passwordCheck)) {
            return u;
        } else {
            throw new IllegalArgumentException("Wrong password.");
        }
    }

    public boolean delete(Long id) {
        if (id <= 0) throw new IllegalArgumentException("Id is invalid.");
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
        } else {
            throw new IllegalArgumentException("User was not found.");
        }
        return true;
    }

    public User update(Long id, User entity) {
        if (id <= 0)
            throw new IllegalArgumentException("Id is invalid.");
        if (entity == null)
            throw new IllegalArgumentException("User is invalid.");
        if (entity.getEmail().isEmpty() ||
                entity.getPassword().isEmpty())
            throw new IllegalArgumentException("One of the params is invalid.");

        User userToChange = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User was not found."));

        userToChange.setEmail(entity.getEmail());
        userToChange.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt()));

        return userRepo.save(userToChange);
    }

    public User changePassword(String email, String oldPassword, String newPassword) {
        if (email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty())
            throw new IllegalArgumentException("One of the params is invalid.");
        User userToChange = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (BCrypt.checkpw(oldPassword, userToChange.getPassword())) {
            userToChange.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            return userRepo.save(userToChange);
        } else {
            throw new IllegalArgumentException("Wrong password.");
        }
    }

    public User getById(Long id) {
        if (id <= 0)
            throw new IllegalArgumentException("Id is invalid.");

        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isEmpty())
            throw new IllegalArgumentException("User was not found.");

        return optionalUser.get();
    }

    public User getByEmail(String email) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("Passed email is invalid.");

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isEmpty())
            throw new IllegalArgumentException("User was not found.");

        return optionalUser.get();
    }

    public List<User> getAll() {
        return userRepo.findAll();
    }


    @PersistenceContext
    private EntityManager entityManager;

    public List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType) {
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(User.class, false, true)
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
                    User user = (User) result[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) result[1];
                    RevisionType type = (RevisionType) result[2];

                    return Map.of(
                            "user", user,
                            "revisionDate", new Date(revisionEntity.getTimestamp()),
                            "revisionType", type.name()
                    );
                })
                .toList();
    }


}
