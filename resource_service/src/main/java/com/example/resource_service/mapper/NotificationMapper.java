package com.example.resource_service.mapper;

import com.example.resource_service.dto.NotificationDto;
import com.example.resource_service.model.Notification;

public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setTimestamp(notification.getTimestamp());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());

        return dto;
    }

    public Notification fromDto(NotificationDto dto) {
        if (dto == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setNotificationId(dto.getNotificationId());
        notification.setTimestamp(dto.getTimestamp());
        notification.setType(dto.getType());
        notification.setMessage(dto.getMessage());

        return notification;
    }
}
