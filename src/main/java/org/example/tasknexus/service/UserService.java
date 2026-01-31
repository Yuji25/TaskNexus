package org.example.tasknexus.service;

import lombok.extern.slf4j.Slf4j;
import org.example.tasknexus.dto.LoginRequest;
import org.example.tasknexus.dto.LoginResponse;
import org.example.tasknexus.dto.RegisterRequest;
import org.example.tasknexus.dto.UserDTO;
import org.example.tasknexus.exception.ResourceNotFoundException;
import org.example.tasknexus.exception.ValidationException;
import org.example.tasknexus.model.Role;
import org.example.tasknexus.model.User;
import org.example.tasknexus.repository.UserRepository;
import org.example.tasknexus.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * UserService
 * Service layer for user-related operations
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    /**
     * Register a new user
     */
    public UserDTO registerUser(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already registered: " + request.getEmail());
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already taken: " + request.getUsername());
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.USER);
        user.setIsActive(true);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }

        return UserDTO.fromEntity(savedUser);
    }

    /**
     * Login user and generate JWT token
     */
    public LoginResponse loginUser(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());

        // Find user by email or username (username can be email or username)
        User user = userRepository.findByEmail(request.getUsername())
                .orElseGet(() -> userRepository.findByUsername(request.getUsername())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found: " + request.getUsername())));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new ValidationException("User account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        log.info("User logged in successfully: {}", user.getId());

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token,
                null, // refreshToken not implemented yet
                "Login successful"
        );
    }

    /**
     * Get user by ID
     */
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return UserDTO.fromEntity(user);
    }

    /**
     * Update user profile
     */
    public UserDTO updateUserProfile(Long userId, UserDTO userDTO) {
        log.info("Updating user profile: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update fields if provided
        if (userDTO.getFullName() != null) {
            user.setFullName(userDTO.getFullName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getProfileImageUrl() != null) {
            user.setProfileImageUrl(userDTO.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully: {}", userId);

        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ValidationException("Invalid current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * Deactivate user account
     */
    public void deactivateUser(Long userId) {
        log.info("Deactivating user account: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User account deactivated: {}", userId);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
