package com.aiprocess.backendonia.repository;

import com.aiprocess.backendonia.domain.ChatMessage;
import com.aiprocess.backendonia.domain.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);
    void deleteBySession(ChatSession session);
}
