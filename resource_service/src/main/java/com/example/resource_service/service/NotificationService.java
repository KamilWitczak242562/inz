package com.example.resource_service.service;

import com.example.resource_service.model.Notification;
import com.example.resource_service.repository.NotificationRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService implements ServiceTemplate<Notification> {
    private final NotificationRepo notificationRepo;

    public List<Notification> getAll() {
        return notificationRepo.findAll();
    }

    public Notification getById(Long id) {
        return notificationRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such notification"));
    }

    public Notification create(Notification notification) {
        return notificationRepo.save(notification);
    }

    public Notification update(Long id, Notification notification) {
        Notification notificationToChange = notificationRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No such notification"));
        notificationToChange.setMessage(notification.getMessage());
        notificationToChange.setType(notification.getType());
        return notificationRepo.save(notificationToChange);
    }

    public List<Notification> getNotificationsByType(String type) {
        return notificationRepo.findAllByType(type);
    }

    public boolean delete(Long id) {
        if (notificationRepo.existsById(id)) {
            notificationRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
