package com.aiprocess.backendonia.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public final class KnowledgeDtos {

    private KnowledgeDtos() {
    }

    public record UrlIngestionRequest(
            @NotBlank String title,
            @NotBlank String url,
            @NotNull UUID categoryId,
            @NotNull UUID templateId
    ) {
    }

    public record AudioTranscriptionResponse(
            String transcription,
            DocumentDtos.DocumentResponse document
    ) {
    }

    public record UploadResponse(
            List<DocumentDtos.DocumentResponse> documents
    ) {
    }
}
