package com.notifyah.auth.service;

import com.notifyah.auth.dto.LoginRequest;
import com.notifyah.auth.dto.LoginResponse;
import com.notifyah.auth.dto.SignupRequest;
import com.notifyah.auth.dto.SignupResponse;
import com.notifyah.common.security.JwtTokenProvider;
import com.notifyah.user.entity.User;
import com.notifyah.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authentication service for user login, signup and JWT token management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticate user and generate JWT token.
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsernameOrEmail());

        // 사용자 인증
        Optional<User> userOpt = userService.authenticateUser(
            request.getUsernameOrEmail(), 
            request.getPassword()
        );

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        User user = userOpt.get();
        
        // JWT 토큰 생성
        String token = jwtTokenProvider.generateEnhancedToken(user);
        
        log.info("User logged in successfully: {} (ID: {})", user.getUsername(), user.getId());
        
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Enum::name).toList())
                .build();
    }

    /**
     * Create new user account and generate JWT token.
     */
    public SignupResponse signup(SignupRequest request) {
        log.info("Signup attempt for user: {}", request.getUsername());

        // 사용자 계정 생성
        User user = userService.createUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFullName()
        );

        // JWT 토큰 생성 (자동 로그인)
        String token = jwtTokenProvider.generateEnhancedToken(user);
        
        log.info("User signed up successfully: {} (ID: {})", user.getUsername(), user.getId());
        
        return SignupResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Enum::name).toList())
                .build();
    }

    /**
     * Generate token for existing user (for testing purposes).
     */
    public LoginResponse generateTokenForUser(Long userId) {
        log.info("Generating token for existing user ID: {}", userId);

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        String token = jwtTokenProvider.generateEnhancedToken(user);
        
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Enum::name).toList())
                .build();
    }
} 