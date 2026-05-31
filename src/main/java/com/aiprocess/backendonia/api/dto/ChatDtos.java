package com.aiprocess.backendonia.api.dto;

import com.aiprocess.backendonia.domain.ChatMode;
import com.aiprocess.backendonia.domain.ChatRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ChatDtos {

    private ChatDtos() {
    }

    public record ChatSessionRequest(
            @NotNull ChatMode mode,
            String title,
            UUID documentId
    ) {
    }

    public record ChatSessionResponse(
            UUID id,
            ChatMode mode,
            String title,
            UUID documentId,
            Instant updatedAt,
            boolean isFinished
    ) {
    }

    public record ChatMessageRequest(
            @NotBlank String content
    ) {
    }

    public record ChatMessageResponse(
            UUID id,
            UUID sessionId,
            ChatRole role,
            String content,
            Instant createdAt
    ) {
    }

    public record ChatReplyResponse(
            ChatSessionResponse session,
            List<ChatMessageResponse> messages
    ) {
    }
}
