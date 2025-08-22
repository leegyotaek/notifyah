package com.notifyah.notification.dto;

import lombok.*;

/**
 * Kafka로 전송되는 알림 이벤트 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationEvent {
    private String eventType;
    private Long senderId;
    private Long targetUserId;
    private Long entityId;
    private String content;
    private String redirectUrl;
} 