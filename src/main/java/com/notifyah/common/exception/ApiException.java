package com.notifyah.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Application-specific runtime exception for NotiFyah API.
 * Carries HTTP status and error code information for proper error responses.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    /**
     * Creates a new ApiException with the specified message, status, and code.
     * 
     * @param message human-readable error message
     * @param status HTTP status code
     * @param code application-specific error code
     */
    public ApiException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }
} 