package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.api.dto.CategoryDtos;
import com.aiprocess.backendonia.api.dto.ChatDtos;
import com.aiprocess.backendonia.api.dto.DocumentDtos;
import com.aiprocess.backendonia.api.dto.TemplateDtos;
import com.aiprocess.backendonia.api.dto.UserDtos;
import com.aiprocess.backendonia.domain.AppUser;
import com.aiprocess.backendonia.domain.Category;
import com.aiprocess.backendonia.domain.ChatMessage;
import com.aiprocess.backendonia.domain.ChatSession;
import com.aiprocess.backendonia.domain.DocumentRecord;
import com.aiprocess.backendonia.domain.TemplateEntity;
import org.springframework.stereotype.Service;

@Service
public class MapperService {

    public CategoryDtos.CategoryResponse toCategoryResponse(Category category) {
        return new CategoryDtos.CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSector(),
                category.getSummary(),
                category.isActive()
        );
    }

    public UserDtos.UserResponse toUserResponse(AppUser user) {
        return new UserDtos.UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getInitials(),
                user.getCategory().getId(),
                user.getCategory().getName(),
                user.isActive()
        );
    }

    public TemplateDtos.TemplateResponse toTemplateResponse(TemplateEntity template) {
        return new TemplateDtos.TemplateResponse(
                template.getId(),
                template.getName(),
                template.getFileType(),
                template.getScope(),
                template.getUpdatedAt()
        );
    }

    public DocumentDtos.DocumentResponse toDocumentResponse(DocumentRecord document) {
        return new DocumentDtos.DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getCategory().getId(),
                document.getCategory().getName(),
                document.getTemplate().getId(),
                document.getTemplate().getName(),
                document.getSourceType(),
                document.getContent(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    public ChatDtos.ChatSessionResponse toChatSessionResponse(ChatSession session) {
        return new ChatDtos.ChatSessionResponse(
                session.getId(),
                session.getMode(),
                session.getTitle(),
                session.getDocument() != null ? session.getDocument().getId() : null,
                session.getUpdatedAt(),
                session.isFinished()
        );
    }

    public ChatDtos.ChatMessageResponse toChatMessageResponse(ChatMessage message) {
        return new ChatDtos.ChatMessageResponse(
                message.getId(),
                message.getSession().getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
