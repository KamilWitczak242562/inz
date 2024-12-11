package com.example.auth_service.model;

import com.example.auth_service.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String firstName;
    private String lastName;
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

}
