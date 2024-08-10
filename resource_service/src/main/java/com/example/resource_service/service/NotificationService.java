package com.example.resource_service.service;

import com.example.resource_service.model.Notification;
import com.example.resource_service.repository.NotificationRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepo notificationRepo;

    public List<Notification> getAllNotifications() {
        return notificationRepo.findAll();
    }

    public Notification getNotificationById(Long id) {
        return notificationRepo.findById(id).orElse(null);
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepo.save(notification);
    }

    public Notification updateNotification(Notification notification) {
        return notificationRepo.save(notification);
    }

    public List<Notification> getNotificationsByType(String type) {
        return notificationRepo.findAllByType(type);
    }

    public boolean deleteNotification(Long id) {
        if (notificationRepo.existsById(id)) {
            notificationRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
