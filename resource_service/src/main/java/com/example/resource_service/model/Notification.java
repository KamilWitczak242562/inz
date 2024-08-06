package com.example.resource_service.model;

import com.example.resource_service.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String timestamp;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String message;
}

