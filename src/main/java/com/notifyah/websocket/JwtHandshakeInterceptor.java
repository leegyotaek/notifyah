package com.notifyah.websocket;

import com.notifyah.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * JWT handshake interceptor for WebSocket authentication.
 * Validates JWT tokens and extracts userId during connection establishment.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                 org.springframework.http.server.ServerHttpResponse response,
                                 org.springframework.web.socket.WebSocketHandler wsHandler,
                                 Map<String, Object> attributes) throws Exception {
        
        String token = extractToken(request);
        
        if (token == null) {
            log.warn("WebSocket handshake rejected: No JWT token found");
            return false;
        }
        
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("WebSocket handshake rejected: Invalid JWT token");
            return false;
        }
        
        try {
            Long userId = jwtTokenProvider.getUserId(token);
            attributes.put("userId", userId);
            log.info("WebSocket handshake accepted for user: {}", userId);
            return true;
        } catch (Exception e) {
            log.warn("WebSocket handshake rejected: Failed to extract userId from token");
            return false;
        }
    }

    @Override
    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                             org.springframework.http.server.ServerHttpResponse response,
                             org.springframework.web.socket.WebSocketHandler wsHandler,
                             Exception exception) {
        // No-op: handshake already completed
    }

    /**
     * Extract JWT token from request with priority order:
     * 1. Authorization header (Bearer token)
     * 2. Sec-WebSocket-Protocol header
     * 3. Query parameter
     */
    private String extractToken(org.springframework.http.server.ServerHttpRequest request) {
        // 1. Check Authorization header
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        
        // 2. Check Sec-WebSocket-Protocol header
        List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");
        if (protocols != null && !protocols.isEmpty()) {
            String protocol = protocols.get(0);
            if (protocol.contains(".")) { // Assume JWT tokens contain dots
                return protocol;
            }
        }
        
        // 3. Check query parameter
        String tokenParam = request.getURI().getQuery();
        if (tokenParam != null && tokenParam.startsWith("token=")) {
            return tokenParam.substring(6);
        }
        
        return null;
    }
} 