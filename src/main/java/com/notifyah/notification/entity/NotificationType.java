package com.notifyah.notification.entity;

/**
 * Enum representing the different types of notifications in the NotiFyah system.
 */
public enum NotificationType {
    /**
     * Notification for a new comment on a post
     */
    NEW_COMMENT,
    
    /**
     * Notification for a new follower
     */
    NEW_FOLLOW,
    
    /**
     * Notification for a post being liked
     */
    POST_LIKED,
    
    /**
     * System-generated notification
     */
    SYSTEM
} 