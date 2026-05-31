package com.aiprocess.backendonia.api.dto;

import com.aiprocess.backendonia.domain.ProposalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class WorkflowDtos {

    private WorkflowDtos() {
    }

    public record ChatMessageInput(
            @NotBlank String role,
            @NotBlank String content
    ) {
    }

    public record ChatRequest(
            @NotEmpty List<ChatMessageInput> messages,
            String attachmentName,
            String attachmentContent
    ) {
    }

    public record ProposalDocumentResponse(
            UUID id,
            String title,
            String content,
            String summary,
            ProposalStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record ChatResponse(
            String type,
            String message,
            ProposalDocumentResponse doc
    ) {
    }

    public record ApproveRequest(
            @NotNull UUID id,
            String conteudo,
            @NotBlank String acao
    ) {
    }

    public record ApproveResponse(
            String type,
            String message,
            ProposalDocumentResponse doc,
            String filePath,
            boolean savedToKnowledgeBase
    ) {
    }
}
