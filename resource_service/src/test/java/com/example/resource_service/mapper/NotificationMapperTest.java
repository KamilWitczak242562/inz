package com.example.resource_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.example.resource_service.dto.NotificationDto;
import com.example.resource_service.model.Notification;
import com.example.resource_service.model.enums.NotificationType;

public class NotificationMapperTest {

    private final NotificationMapper mapper = new NotificationMapper();

    @Test
    void shouldConvertNotificationToDto() {
        // Given
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setTimestamp("2024-08-06T12:00:00Z");
        notification.setType(NotificationType.INFO);
        notification.setMessage("This is a test notification.");

        // When
        NotificationDto dto = mapper.toDto(notification);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getNotificationId()).isEqualTo(1L);
        assertThat(dto.getTimestamp()).isEqualTo("2024-08-06T12:00:00Z");
        assertThat(dto.getType()).isEqualTo(NotificationType.INFO);
        assertThat(dto.getMessage()).isEqualTo("This is a test notification.");
    }

    @Test
    void shouldConvertDtoToNotification() {
        // Given
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(1L);
        dto.setTimestamp("2024-08-06T12:00:00Z");
        dto.setType(NotificationType.INFO);
        dto.setMessage("This is a test notification.");

        // When
        Notification notification = mapper.fromDto(dto);

        // Then
        assertThat(notification).isNotNull();
        assertThat(notification.getNotificationId()).isEqualTo(1L);
        assertThat(notification.getTimestamp()).isEqualTo("2024-08-06T12:00:00Z");
        assertThat(notification.getType()).isEqualTo(NotificationType.INFO);
        assertThat(notification.getMessage()).isEqualTo("This is a test notification.");
    }
}
