package org.example.tasknexus.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.dto.*;
import org.example.tasknexus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * REST controller for authentication endpoints
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Register new user
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Register endpoint called for email: {}", request.getEmail());

        try {
            UserDTO userDTO = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", userDTO));
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Login user
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        log.info("Login endpoint called for: {}", request.getUsername());

        try {
            LoginResponse loginResponse = userService.loginUser(request);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Login successful", loginResponse));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage(), 401));
        }
    }

    /**
     * Logout user (client-side token removal)
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser() {
        log.info("Logout endpoint called");

        return ResponseEntity.ok()
                .body(ApiResponse.success("Logged out successfully", null));
    }

    /**
     * Check if email is available
     * GET /api/v1/auth/check-email/{email}
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<ApiResponse> checkEmailAvailability(@PathVariable String email) {
        log.info("Check email availability: {}", email);

        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok()
                .body(new ApiResponse(
                        "success",
                        exists ? "Email already registered" : "Email is available",
                        exists,
                        200
                ));
    }

    /**
     * Check if username is available
     * GET /api/v1/auth/check-username/{username}
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse> checkUsernameAvailability(@PathVariable String username) {
        log.info("Check username availability: {}", username);

        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok()
                .body(new ApiResponse(
                        "success",
                        exists ? "Username already taken" : "Username is available",
                        exists,
                        200
                ));
    }
}
