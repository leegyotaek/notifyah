package com.notifyah.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

/**
 * Standard JSON error response for NotiFyah API.
 * Provides consistent error format across all endpoints.
 */
@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private final String timestamp;
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;

    /**
     * Static builder for creating ErrorResponse instances.
     * 
     * @param status HTTP status code
     * @param code application-specific error code
     * @param message human-readable error message
     * @param path request URI path
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(HttpStatus status, String code, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(path)
                .build();
    }
} 