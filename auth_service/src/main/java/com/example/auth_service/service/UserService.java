package com.example.auth_service.service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

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

    public User changePassword(String email, String oldPassword, String newPassword){
        if (email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) throw new IllegalArgumentException("One of the params is invalid.");
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
}
