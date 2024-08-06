package com.example.resource_service.dto;

import com.example.resource_service.model.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationDto {
    private Long notificationId;

    private String timestamp;
    private NotificationType type;
    private String message;
}
