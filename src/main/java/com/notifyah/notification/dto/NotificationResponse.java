package com.notifyah.notification.dto;

import com.notifyah.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notification responses in the NotiFyah API.
 * Provides a clean representation of notifications for clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String type;
    private String content;
    private String redirectUrl;
    private boolean read;
    private LocalDateTime createdAt;

    /**
     * Maps a Notification entity to NotificationResponse DTO.
     * 
     * @param notification the notification entity
     * @return NotificationResponse DTO
     */
    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType().name(),
                notification.getContent(),
                notification.getRedirectUrl(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
} 