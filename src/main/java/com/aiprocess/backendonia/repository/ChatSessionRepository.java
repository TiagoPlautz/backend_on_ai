package com.aiprocess.backendonia.repository;

import com.aiprocess.backendonia.domain.AppUser;
import com.aiprocess.backendonia.domain.ChatMode;
import com.aiprocess.backendonia.domain.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByUserAndModeOrderByUpdatedAtDesc(AppUser user, ChatMode mode);
}
