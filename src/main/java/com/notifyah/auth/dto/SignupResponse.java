package com.notifyah.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Signup response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
} 