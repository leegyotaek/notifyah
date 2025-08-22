package com.notifyah.user.service;

import com.notifyah.user.entity.User;
import com.notifyah.user.entity.UserRole;
import com.notifyah.user.entity.UserStatus;
import com.notifyah.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Service for user management operations.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user account.
     */
    public User createUser(String username, String email, String password, String fullName) {
        log.info("Creating new user: {}", username);

        // 중복 검사
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // 사용자 생성
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(UserRole.USER))
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully: {} (ID: {})", username, savedUser.getId());
        
        return savedUser;
    }

    /**
     * Authenticate user by username/email and password.
     */
    public Optional<User> authenticateUser(String usernameOrEmail, String password) {
        log.debug("Authenticating user: {}", usernameOrEmail);

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // 계정 상태 확인
            if (!user.isEnabled()) {
                log.warn("User account is not enabled: {}", usernameOrEmail);
                return Optional.empty();
            }
            
            // 비밀번호 확인
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 로그인 시간 업데이트
                user.updateLastLogin();
                userRepository.save(user);
                
                log.info("User authenticated successfully: {}", usernameOrEmail);
                return Optional.of(user);
            } else {
                log.warn("Invalid password for user: {}", usernameOrEmail);
            }
        } else {
            log.warn("User not found: {}", usernameOrEmail);
        }
        
        return Optional.empty();
    }

    /**
     * Find user by ID.
     */
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Find user by username.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Update user profile.
     */
    public User updateProfile(Long userId, String fullName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setFullName(fullName);
        user.setEmail(email);
        
        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {} (ID: {})", updatedUser.getUsername(), userId);
        
        return updatedUser;
    }

    /**
     * Change user password.
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // 새 비밀번호 설정
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password changed for user: {} (ID: {})", user.getUsername(), userId);
    }

    /**
     * Update user roles (admin only).
     */
    public User updateUserRoles(Long userId, Set<UserRole> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        
        log.info("User roles updated: {} (ID: {}) - New roles: {}", 
                updatedUser.getUsername(), userId, roles);
        
        return updatedUser;
    }

    /**
     * Activate user account.
     */
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.activate();
        userRepository.save(user);
        
        log.info("User account activated: {} (ID: {})", user.getUsername(), userId);
    }

    /**
     * Deactivate user account.
     */
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.deactivate();
        userRepository.save(user);
        
        log.info("User account deactivated: {} (ID: {})", user.getUsername(), userId);
    }
} 