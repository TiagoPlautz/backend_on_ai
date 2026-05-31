package com.aiprocess.backendonia.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public final class CategoryDtos {

    private CategoryDtos() {
    }

    public record CategoryRequest(
            @NotBlank String name,
            String sector,
            String summary
    ) {
    }

    public record CategoryStatusRequest(boolean isActive) {
    }

    public record CategoryResponse(
            UUID id,
            String name,
            String sector,
            String summary,
            boolean isActive
    ) {
    }
}
