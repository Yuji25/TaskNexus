package org.example.tasknexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse
 * DTO for user login response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String username;
    private String email;
    private String token;
    private String refreshToken;
    private String message;
}
