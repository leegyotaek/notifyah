package com.notifyah.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyah.notification.dto.NotificationEvent;
import com.notifyah.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka listener service for processing notification events.
 * Consumes messages from Kafka topics and logs the received events.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    /**
     * Listens to the "comment-created" Kafka topic and processes notification events.
     * 
     * @param message JSON string representing the notification event
     */
    @KafkaListener(topics = "comment-created", groupId = "notification-group")
    public void handleCommentCreated(String message) {
        try {
            log.info("Received Kafka message: {}", message);
            
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);
            log.info("Parsed notification event: {}", event);
            
            notificationService.createNotification(event);
            
        } catch (Exception e) {
            log.error("Error processing notification event: {}", message, e);
        }
    }
} 