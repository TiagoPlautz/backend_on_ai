package com.aiprocess.backendonia.api.dto;

import com.aiprocess.backendonia.domain.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class DocumentDtos {

    private DocumentDtos() {
    }

    public record DocumentRequest(
            @NotBlank String title,
            @NotNull UUID categoryId,
            @NotNull UUID templateId,
            @NotNull SourceType sourceType,
            String content
    ) {
    }

    public record DocumentResponse(
            UUID id,
            String title,
            UUID categoryId,
            String categoryName,
            UUID templateId,
            String templateName,
            SourceType sourceType,
            String content,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record DocumentMetricsResponse(
            long categories,
            long users,
            long templates,
            long documents,
            long chatSessions,
            Map<SourceType, Long> documentsBySource
    ) {
    }
}
