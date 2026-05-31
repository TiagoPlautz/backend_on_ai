package com.aiprocess.backendonia.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public final class TemplateDtos {

    private TemplateDtos() {
    }

    public record TemplateRequest(
            @NotBlank String name,
            @NotBlank String fileType,
            @NotBlank String scope
    ) {
    }

    public record TemplateResponse(
            UUID id,
            String name,
            String fileType,
            String scope,
            Instant updatedAt
    ) {
    }
}
