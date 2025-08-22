package com.notifyah.notification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyah.notification.dto.NotificationEvent;
import com.notifyah.notification.dto.NotificationRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for users to send notifications directly
 */
@RestController
@RequestMapping("/api/notifications/send")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class NotificationSenderController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "comment-created";

    /**
     * Send notification directly by user
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            log.info("User notification send request: {}", request);

            // Create NotificationEvent
            NotificationEvent event = NotificationEvent.builder()
                    .eventType(request.getEventType())
                    .senderId(request.getSenderId())
                    .targetUserId(request.getTargetUserId())
                    .entityId(request.getEntityId())
                    .content(request.getContent())
                    .redirectUrl(request.getRedirectUrl())
                    .build();

            // Serialize to JSON and send to Kafka
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, jsonMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Notification sent successfully: {} -> user {}", 
                                    event.getContent(), event.getTargetUserId());
                        } else {
                            log.error("Notification send failed: {}", ex.getMessage());
                        }
                    });

            return ResponseEntity.ok("Notification sent successfully.");
            
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("JSON serialization error occurred while sending notification.");
        } catch (Exception e) {
            log.error("Error occurred while sending notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Error occurred while sending notification: " + e.getMessage());
        }
    }

    /**
     * Quick notification send (simple form)
     */
    @PostMapping("/quick")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> sendQuickNotification(
            @RequestParam String eventType,
            @RequestParam Long targetUserId,
            @RequestParam String content) {
        
        try {
            log.info("Quick notification send: {} -> user {}", content, targetUserId);

            NotificationEvent event = NotificationEvent.builder()
                    .eventType(eventType)
                    .senderId(1L) // Default value
                    .targetUserId(targetUserId)
                    .entityId(System.currentTimeMillis() % 1000) // Random entity ID
                    .content(content)
                    .redirectUrl("/posts/" + (System.currentTimeMillis() % 1000))
                    .build();

            // Serialize to JSON and send to Kafka
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, jsonMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Quick notification sent successfully: {} -> user {}", 
                                    event.getContent(), event.getTargetUserId());
                        } else {
                            log.error("Quick notification send failed: {}", ex.getMessage());
                        }
                    });

            return ResponseEntity.ok("Quick notification sent successfully.");
            
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("JSON serialization error occurred while sending quick notification.");
        } catch (Exception e) {
            log.error("Error occurred while sending quick notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Error occurred while sending quick notification: " + e.getMessage());
        }
    }
} 