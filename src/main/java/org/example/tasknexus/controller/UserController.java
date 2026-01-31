package org.example.tasknexus.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.dto.ApiResponse;
import org.example.tasknexus.dto.UserDTO;
import org.example.tasknexus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 * REST controller for user-related endpoints
 */
@Slf4j
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user profile
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getCurrentUser(HttpServletRequest request) {
        log.info("Get current user endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");
            UserDTO userDTO = userService.getUserById(userId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User fetched successfully", userDTO));
        } catch (Exception e) {
            log.error("Get user error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), 404));
        }
    }

    /**
     * Get user by ID
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        log.info("Get user by ID: {}", id);

        try {
            UserDTO userDTO = userService.getUserById(id);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User fetched successfully", userDTO));
        } catch (Exception e) {
            log.error("Get user by ID error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), 404));
        }
    }

    /**
     * Update user profile
     * PUT /api/v1/users/me
     */
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateUserProfile(
            HttpServletRequest request,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("Update user profile endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");
            UserDTO updatedUser = userService.updateUserProfile(userId, userDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User profile updated successfully", updatedUser));
        } catch (Exception e) {
            log.error("Update user error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Change user password
     * PUT /users/me/password
     */
    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> changePassword(
            HttpServletRequest request,
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Change password endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");
            userService.changePassword(userId, changePasswordRequest.getCurrentPassword(),
                    changePasswordRequest.getNewPassword());
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Password changed successfully", null));
        } catch (Exception e) {
            log.error("Change password error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Deactivate user account
     * DELETE /api/v1/users/me
     */
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deactivateAccount(HttpServletRequest request) {
        log.info("Deactivate account endpoint called");

        try {
            Long userId = (Long) request.getAttribute("userId");
            userService.deactivateUser(userId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Account deactivated successfully", null));
        } catch (Exception e) {
            log.error("Deactivate account error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Change password request DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }
}
