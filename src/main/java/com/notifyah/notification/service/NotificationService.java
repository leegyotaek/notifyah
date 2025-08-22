package com.notifyah.notification.service;

import com.notifyah.notification.dto.NotificationEvent;
import com.notifyah.notification.dto.NotificationResponse;
import com.notifyah.notification.entity.Notification;
import com.notifyah.notification.entity.NotificationType;
import com.notifyah.notification.repository.NotificationRepository;
import com.notifyah.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing notifications in the NotiFyah system.
 * Handles the creation and persistence of notifications.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketHandler webSocketHandler;

    /**
     * Creates a new notification from a notification event.
     * Maps the event data to a Notification entity and persists it.
     * 
     * @param event the notification event to process
     */
    public void createNotification(NotificationEvent event) {
        try {
            log.info("Creating notification for event: {}", event);
            
            Notification notification = Notification.builder()
                    .recipientId(event.getTargetUserId())
                    .type(mapEventTypeToNotificationType(event.getEventType()))
                    .content(event.getContent())
                    .redirectUrl(event.getRedirectUrl())
                    .isRead(false)
                    .build();
            
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Successfully saved notification with ID: {}", savedNotification.getId());
            
            // Send real-time notification via WebSocket
            try {
                webSocketHandler.sendToUser(event.getTargetUserId(), savedNotification);
                log.info("Notification sent to user {} via WebSocket", event.getTargetUserId());
            } catch (Exception e) {
                log.warn("Failed to send notification to user {} via WebSocket: {}", 
                        event.getTargetUserId(), e.getMessage());
                // Don't rethrow - WebSocket delivery failure shouldn't affect DB save
            }
            
        } catch (Exception e) {
            log.error("Error creating notification for event: {}", event, e);
            throw new RuntimeException("Failed to create notification", e);
        }
    }

    /**
     * Maps the event type string to NotificationType enum.
     * 
     * @param eventType the event type string
     * @return corresponding NotificationType enum value
     */
    private NotificationType mapEventTypeToNotificationType(String eventType) {
        try {
            return NotificationType.valueOf(eventType);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}. Defaulting to SYSTEM", eventType);
            return NotificationType.SYSTEM;
        }
    }

    /**
     * Get paginated notifications for a user.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of notification responses
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        log.debug("Fetching notifications for user: {} with pagination: {}", userId, pageable);
        return notificationRepository.findByRecipientId(userId, pageable)
                .map(NotificationResponse::fromEntity);
    }

    /**
     * Count unread notifications for a user.
     * 
     * @param userId the user ID
     * @return count of unread notifications
     */
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        log.debug("Counting unread notifications for user: {}", userId);
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    /**
     * Mark a specific notification as read.
     * 
     * @param userId the user ID
     * @param id the notification ID
     */
    @Transactional
    public void markAsRead(Long userId, Long id) {
        log.debug("Marking notification {} as read for user: {}", id, userId);
        Notification notification = notificationRepository.findByIdAndRecipientId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found or not owned by user"));
        
        notification.setRead(true);
        notificationRepository.save(notification);
        log.info("Notification {} marked as read for user: {}", id, userId);
    }

    /**
     * Mark all unread notifications as read for a user.
     * 
     * @param userId the user ID
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        log.debug("Marking all notifications as read for user: {}", userId);
        int updatedCount = notificationRepository.markAllAsRead(userId);
        log.info("Marked {} notifications as read for user: {}", updatedCount, userId);
    }

    /**
     * Delete a specific notification.
     * 
     * @param userId the user ID
     * @param id the notification ID
     */
    @Transactional
    public void delete(Long userId, Long id) {
        log.debug("Deleting notification {} for user: {}", id, userId);
        Notification notification = notificationRepository.findByIdAndRecipientId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found or not owned by user"));
        
        notificationRepository.delete(notification);
        log.info("Notification {} deleted for user: {}", id, userId);
    }
} 