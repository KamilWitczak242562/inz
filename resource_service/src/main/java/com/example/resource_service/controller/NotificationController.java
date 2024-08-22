package com.example.resource_service.controller;

import com.example.resource_service.model.Notification;
import com.example.resource_service.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getNotifications() {
        List<Notification> notificationList = notificationService.getAll();
        return ResponseEntity.ok().body(Map.of("response", notificationList,
                "ok", true));
    }

    @GetMapping("/getByType/{type}")
    public ResponseEntity<?> getNotificationsByType(@PathVariable String type) {
        List<Notification> notificationList = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok().body(Map.of("response", notificationList,
                "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        return ResponseEntity.ok().body(Map.of("response", notification,
                "ok", true));
    }

    @PostMapping("/new")
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        Notification newNotification = notificationService.create(notification);
        return ResponseEntity.ok().body(Map.of("response", newNotification,
                "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        Notification newNotification = notificationService.update(id, notification);
        return ResponseEntity.ok().body(Map.of("response", newNotification,
                "ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        boolean isDeleted = notificationService.delete(id);
        return ResponseEntity.ok().body(Map.of("response", isDeleted,
                "ok", true));
    }
}
