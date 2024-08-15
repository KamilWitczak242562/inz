package com.example.resource_service.controller;

import com.example.resource_service.model.Notification;
import com.example.resource_service.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/notification")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getNotifications() {
        List<Notification> notificationList = notificationService.getAll();
        return ResponseEntity.ok(notificationList);
    }

    @GetMapping("/getByType/{type}")
    public ResponseEntity<?> getNotificationsByType(@PathVariable String type) {
        List<Notification> notificationList = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notificationList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        Notification newNotification = notificationService.create(notification);
        return ResponseEntity.ok(newNotification);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        Notification newNotification = notificationService.update(id, notification);
        return ResponseEntity.ok(newNotification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        boolean isDeleted = notificationService.delete(id);
        return ResponseEntity.ok(isDeleted);
    }
}
