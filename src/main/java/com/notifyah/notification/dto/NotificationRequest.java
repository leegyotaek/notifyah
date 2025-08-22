package com.notifyah.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 알림 전송 요청을 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String eventType;
    private Long senderId;
    private Long targetUserId;
    private Long entityId;
    private String content;
    private String redirectUrl;
} 