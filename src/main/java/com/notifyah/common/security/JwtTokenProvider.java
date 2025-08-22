package com.notifyah.common.security;

import com.notifyah.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enhanced JWT token provider for NotiFyah.
 * Handles token generation, validation and user information extraction.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey secretKey;

    /**
     * Initialize the secret key from the configured secret.
     */
    private SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
        return secretKey;
    }

    /**
     * Generate JWT token with basic user information.
     * 
     * @param userId the user ID
     * @return JWT token string
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate enhanced JWT token with full user information.
     * 
     * @param user the user entity
     * @return JWT token string with rich user information
     */
    public String generateEnhancedToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("fullName", user.getFullName());
        claims.put("roles", user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));
        claims.put("status", user.getStatus().name());
        claims.put("emailVerified", user.isEmailVerified());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuer("notifyah")
                .setAudience("notifyah-api")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract userId from JWT token.
     * 
     * @param token the JWT token
     * @return userId as Long
     * @throws IllegalArgumentException if token is invalid
     */
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
            
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            log.warn("Failed to extract userId from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    /**
     * Extract username from JWT token.
     * 
     * @param token the JWT token
     * @return username as String
     */
    public String getUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.get("username", String.class);
        } catch (Exception e) {
            log.debug("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract user roles from JWT token.
     * 
     * @param token the JWT token
     * @return roles as Set<String>
     */
    @SuppressWarnings("unchecked")
    public Set<String> getUserRoles(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
            
            return (Set<String>) claims.get("roles");
        } catch (Exception e) {
            log.debug("Failed to extract roles from token: {}", e.getMessage());
            return Set.of();
        }
    }

    /**
     * Validate JWT token signature and expiration.
     * 
     * @param token the JWT token
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired.
     * 
     * @param token the JWT token
     * @return true if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }
} 