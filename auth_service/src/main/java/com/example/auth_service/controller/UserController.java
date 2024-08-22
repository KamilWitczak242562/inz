package com.example.auth_service.controller;

import com.example.auth_service.model.User;
import com.example.auth_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User createdUser = userService.create(user);
        return ResponseEntity.ok().body(Map.of("response", createdUser, "ok", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        User user = userService.logInUser(credentials.get("email"), credentials.get("password"));
        return ResponseEntity.ok().body(Map.of("response", user, "ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.delete(id);
        return ResponseEntity.ok().body(Map.of("response", isDeleted, "ok", true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.update(id, user);
        return ResponseEntity.ok().body(Map.of("response", updatedUser, "ok", true));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        User updatedUser = userService.changePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok().body(Map.of("response", updatedUser, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok().body(Map.of("response", user, "ok", true));
    }

    @PostMapping("/email")
    public ResponseEntity<?> getUserByEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userService.getByEmail(email);
        return ResponseEntity.ok().body(Map.of("response", user, "ok", true));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok().body(Map.of("response", users, "ok", true));
    }
}
