package com.notifyah.debug.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyah.notification.TopicNames;
import com.notifyah.notification.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Kafka 테스트를 위한 디버그 이벤트 컨트롤러
 */
@RestController
@RequestMapping("/debug/events")
@RequiredArgsConstructor
@Slf4j
public class DebugEventController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 댓글 생성 이벤트를 Kafka로 발행
     */
    @PostMapping("/comment")
    public ResponseEntity<Map<String, String>> publishCommentEvent(@RequestBody NotificationEvent event) {
        try {
            log.info("디버그 댓글 이벤트 발행 요청: {}", event);
            
            // NotificationEvent를 JSON으로 직렬화
            String jsonMessage = objectMapper.writeValueAsString(event);
            log.info("Kafka로 발행할 JSON 메시지: {}", jsonMessage);
            
            // Kafka 토픽으로 메시지 발행
            kafkaTemplate.send(TopicNames.COMMENT_CREATED, jsonMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("댓글 이벤트가 성공적으로 Kafka로 발행되었습니다. 토픽: {}, 파티션: {}, 오프셋: {}", 
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("댓글 이벤트 Kafka 발행 실패: {}", ex.getMessage(), ex);
                        }
                    });
            
            Map<String, String> response = Map.of(
                    "status", "PUBLISHED",
                    "topic", TopicNames.COMMENT_CREATED
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("댓글 이벤트 발행 중 오류 발생: {}", e.getMessage(), e);
            Map<String, String> errorResponse = Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 