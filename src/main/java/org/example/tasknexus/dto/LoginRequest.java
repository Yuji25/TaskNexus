package org.example.tasknexus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequest
 * DTO for user login request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email or username cannot be empty")
    private String emailOrUsername;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
