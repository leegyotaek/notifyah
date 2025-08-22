package com.notifyah.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyah.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time notification delivery.
 * Manages user sessions and pushes notifications to connected clients.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    
    /**
     * Thread-safe map to store user WebSocket sessions.
     * Key: userId, Value: WebSocketSession
     */
    private final ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    /**
     * Called when a WebSocket connection is established.
     * Extracts the userId from session attributes and stores the session.
     * 
     * @param session the WebSocket session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId != null) {
                userSessions.put(userId, session);
                log.info("WebSocket connection established for user: {}", userId);
            } else {
                log.warn("WebSocket connection established but no userId found in session attributes");
            }
        } catch (Exception e) {
            log.error("Error establishing WebSocket connection", e);
        }
    }

    /**
     * Called when a WebSocket connection is closed.
     * Removes the user session from the session map.
     * 
     * @param session the WebSocket session
     * @param status the close status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId != null) {
                userSessions.remove(userId);
                log.info("WebSocket connection closed for user: {}", userId);
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket connection close", e);
        }
    }

    /**
     * Sends a notification to a specific user via WebSocket.
     * 
     * @param userId the ID of the user to send the notification to
     * @param notification the notification to send
     */
    public void sendToUser(Long userId, Notification notification) {
        WebSocketSession session = userSessions.get(userId);
        
        if (session == null) {
            log.debug("User {} is not connected, notification will not be delivered", userId);
            return;
        }
        
        if (!session.isOpen()) {
            log.debug("User {} session is closed, removing from session map", userId);
            userSessions.remove(userId);
            return;
        }
        
        try {
            String json = objectMapper.writeValueAsString(notification);
            TextMessage message = new TextMessage(json);
            session.sendMessage(message);
            log.info("Notification sent to user {}: {}", userId, notification.getId());
        } catch (IOException e) {
            log.error("Error sending notification to user {}: {}", userId, notification.getId(), e);
            // Remove the session if there's an error sending
            userSessions.remove(userId);
        }
    }

    /**
     * Checks if a user is currently connected via WebSocket.
     * 
     * @param userId the ID of the user to check
     * @return true if the user is connected and their session is open
     */
    public boolean isUserConnected(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * Gets the number of currently connected users.
     * 
     * @return the number of active WebSocket sessions
     */
    public int getConnectedUsersCount() {
        return userSessions.size();
    }

    /**
     * Extracts the userId from the WebSocket session attributes.
     * Assumes the userId was stored during the handshake process.
     * 
     * @param session the WebSocket session
     * @return the userId, or null if not found
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        try {
            Object userIdObj = session.getAttributes().get("userId");
            if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                return Long.valueOf((String) userIdObj);
            }
        } catch (Exception e) {
            log.warn("Error extracting userId from session attributes", e);
        }
        return null;
    }
} 