package com.example.resource_service.service;

import com.example.resource_service.model.Notification;
import com.example.resource_service.model.enums.NotificationType;
import com.example.resource_service.repository.NotificationRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private NotificationRepo notificationRepo;

    private AutoCloseable autoCloseable;
    private NotificationService notificationService;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(notificationRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testGetAllNotifications() {
        Notification notification1 = new Notification();
        notification1.setNotificationId(1L);
        notification1.setTimestamp("2024-08-10T10:00:00");
        notification1.setType(NotificationType.INFO);
        notification1.setMessage("Message 1");

        Notification notification2 = new Notification();
        notification2.setNotificationId(2L);
        notification2.setTimestamp("2024-08-10T11:00:00");
        notification2.setType(NotificationType.ERROR);
        notification2.setMessage("Message 2");

        when(notificationRepo.findAll()).thenReturn(Arrays.asList(notification1, notification2));

        List<Notification> notifications = notificationService.getAllNotifications();

        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertEquals(notification1, notifications.get(0));
        assertEquals(notification2, notifications.get(1));
    }

    @Test
    public void testGetNotificationById() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setTimestamp("2024-08-10T10:00:00");
        notification.setType(NotificationType.INFO);
        notification.setMessage("Message");

        when(notificationRepo.findById(1L)).thenReturn(Optional.of(notification));

        Notification foundNotification = notificationService.getNotificationById(1L);

        assertNotNull(foundNotification);
        assertEquals(notification, foundNotification);
    }

    @Test
    public void testSaveNotification() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setTimestamp("2024-08-10T10:00:00");
        notification.setType(NotificationType.INFO);
        notification.setMessage("Message");

        when(notificationRepo.save(notification)).thenReturn(notification);

        Notification savedNotification = notificationService.saveNotification(notification);


        assertNotNull(savedNotification);
        assertEquals(notification, savedNotification);
    }

    @Test
    public void testUpdateNotification() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setTimestamp("2024-08-10T10:00:00");
        notification.setType(NotificationType.INFO);
        notification.setMessage("Updated Message");

        when(notificationRepo.save(notification)).thenReturn(notification);

        Notification updatedNotification = notificationService.updateNotification(notification);

        assertNotNull(updatedNotification);
        assertEquals(notification, updatedNotification);
    }

    @Test
    public void testGetNotificationsByType() {
        Notification notification1 = new Notification();
        notification1.setNotificationId(1L);
        notification1.setTimestamp("2024-08-10T10:00:00");
        notification1.setType(NotificationType.INFO);
        notification1.setMessage("Message 1");

        Notification notification2 = new Notification();
        notification2.setNotificationId(2L);
        notification2.setTimestamp("2024-08-10T11:00:00");
        notification2.setType(NotificationType.INFO);
        notification2.setMessage("Message 2");

        when(notificationRepo.findAllByType(NotificationType.INFO.toString())).thenReturn(Arrays.asList(notification1, notification2));

        List<Notification> notifications = notificationService.getNotificationsByType(NotificationType.INFO.toString());

        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertTrue(notifications.contains(notification1));
        assertTrue(notifications.contains(notification2));
    }

    @Test
    public void testDeleteNotificationSuccess() {
        when(notificationRepo.existsById(1L)).thenReturn(true);

        boolean result = notificationService.deleteNotification(1L);

        assertTrue(result);
        verify(notificationRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteNotificationFailure() {
        when(notificationRepo.existsById(1L)).thenReturn(false);

        boolean result = notificationService.deleteNotification(1L);

        assertFalse(result);
        verify(notificationRepo, never()).deleteById(1L);
    }
}