package com.notifyah.auth.controller;

import com.notifyah.auth.dto.LoginRequest;
import com.notifyah.auth.dto.LoginResponse;
import com.notifyah.auth.dto.SignupRequest;
import com.notifyah.auth.dto.SignupResponse;
import com.notifyah.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for user registration, login and JWT token management.
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * User registration endpoint.
     * 
     * @param request signup request containing user information
     * @return JWT token response with user details
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup request for username: {}", request.getUsername());
        
        SignupResponse response = authService.signup(request);
        log.info("User registered successfully: {} (ID: {})", request.getUsername(), response.getUserId());
        
        return ResponseEntity.ok(response);
    }

    /**
     * User login endpoint.
     * 
     * @param request login request containing credentials
     * @return JWT token response with user details
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsernameOrEmail());
        
        LoginResponse response = authService.login(request);
        log.info("User logged in successfully: {} (ID: {})", response.getUsername(), response.getUserId());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate token for existing user (for testing purposes).
     * 
     * @param userId the user ID
     * @return JWT token response with user details
     */
    @GetMapping("/token/{userId}")
    public ResponseEntity<LoginResponse> generateToken(@PathVariable Long userId) {
        log.info("Token generation request for userId: {}", userId);
        
        LoginResponse response = authService.generateTokenForUser(userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for authentication service.
     * 
     * @return service status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }
} 