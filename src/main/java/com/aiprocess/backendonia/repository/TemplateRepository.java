package com.aiprocess.backendonia.repository;

import com.aiprocess.backendonia.domain.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {
}
