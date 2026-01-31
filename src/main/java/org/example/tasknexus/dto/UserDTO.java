package org.example.tasknexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasknexus.model.Role;
import org.example.tasknexus.model.User;

import java.time.LocalDateTime;

/**
 * UserDTO
 * Data Transfer Object for User entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String username;
    private String fullName;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String phoneNumber;
    private String profileImageUrl;

    /**
     * Convert User entity to UserDTO
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileImageUrl(user.getProfileImageUrl());

        return dto;
    }

    /**
     * Convert UserDTO to User entity
     */
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setUsername(this.username);
        user.setFullName(this.fullName);
        user.setRole(this.role);
        user.setIsActive(this.isActive);
        user.setPhoneNumber(this.phoneNumber);
        user.setProfileImageUrl(this.profileImageUrl);

        return user;
    }
}
