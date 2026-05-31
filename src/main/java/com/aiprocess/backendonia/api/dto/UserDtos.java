package com.aiprocess.backendonia.api.dto;

import com.aiprocess.backendonia.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public final class UserDtos {

    private UserDtos() {
    }

    public record UserRequest(
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotNull UserRole role,
            String initials,
            @NotNull UUID categoryId
    ) {
    }

    public record UserStatusRequest(boolean isActive) {
    }

    public record UserResponse(
            UUID id,
            String name,
            String email,
            UserRole role,
            String initials,
            UUID categoryId,
            String categoryName,
            boolean isActive
    ) {
    }
}
