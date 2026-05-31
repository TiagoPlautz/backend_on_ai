package com.aiprocess.backendonia.repository;

import com.aiprocess.backendonia.domain.Category;
import com.aiprocess.backendonia.domain.DocumentRecord;
import com.aiprocess.backendonia.domain.SourceType;
import com.aiprocess.backendonia.domain.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentRecord, UUID> {
    List<DocumentRecord> findTop5ByOrderByCreatedAtDesc();
    List<DocumentRecord> findTop5ByOrderByUpdatedAtDesc();
    long countByCategory(Category category);
    long countByTemplate(TemplateEntity template);
    long countBySourceType(SourceType sourceType);
}
