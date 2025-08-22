package com.notifyah.common.security;

import java.security.Principal;

/**
 * Immutable principal representing an authenticated user in NotiFyah.
 * Holds the user ID and implements Principal interface for Spring Security.
 */
public record UserPrincipal(Long userId) implements Principal {

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
} 