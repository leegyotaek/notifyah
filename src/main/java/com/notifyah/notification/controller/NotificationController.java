package com.notifyah.notification.controller;

import com.notifyah.notification.dto.NotificationResponse;
import com.notifyah.notification.service.NotificationService;
import com.notifyah.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for notification management in NotiFyah.
 * Provides endpoints for CRUD operations on notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get paginated notifications for the authenticated user.
     * 
     * @param page page number (default: 0)
     * @param size page size (default: 20)
     * @param user authenticated user principal
     * @return page of notifications
     */
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal user) {
        
        Long userId = user.userId();
        Pageable pageable = PageRequest.of(page, size);
        
        log.info("Fetching notifications for user: {} with pagination: page={}, size={}", userId, page, size);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, pageable);
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get count of unread notifications for the authenticated user.
     * 
     * @param user authenticated user principal
     * @return JSON with unread count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserPrincipal user) {
        Long userId = user.userId();
        
        log.info("Counting unread notifications for user: {}", userId);
        long count = notificationService.countUnread(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark a specific notification as read.
     * 
     * @param id notification ID
     * @param user authenticated user principal
     * @return 204 No Content on success
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        Long userId = user.userId();
        
        log.info("Marking notification {} as read for user: {}", id, userId);
        notificationService.markAsRead(userId, id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Mark all unread notifications as read for the authenticated user.
     * 
     * @param user authenticated user principal
     * @return 204 No Content on success
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal user) {
        Long userId = user.userId();
        
        log.info("Marking all notifications as read for user: {}", userId);
        notificationService.markAllAsRead(userId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a specific notification.
     * 
     * @param id notification ID
     * @param user authenticated user principal
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        Long userId = user.userId();
        
        log.info("Deleting notification {} for user: {}", id, userId);
        notificationService.delete(userId, id);
        
        return ResponseEntity.noContent().build();
    }

} 