package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.api.dto.DocumentDtos;
import com.aiprocess.backendonia.domain.DocumentRecord;
import com.aiprocess.backendonia.domain.SourceType;
import com.aiprocess.backendonia.repository.AppUserRepository;
import com.aiprocess.backendonia.repository.CategoryRepository;
import com.aiprocess.backendonia.repository.ChatSessionRepository;
import com.aiprocess.backendonia.repository.DocumentRepository;
import com.aiprocess.backendonia.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryService categoryService;
    private final TemplateService templateService;
    private final MapperService mapperService;
    private final CategoryRepository categoryRepository;
    private final AppUserRepository userRepository;
    private final TemplateRepository templateRepository;
    private final ChatSessionRepository chatSessionRepository;

    public DocumentService(DocumentRepository documentRepository,
                           CategoryService categoryService,
                           TemplateService templateService,
                           MapperService mapperService,
                           CategoryRepository categoryRepository,
                           AppUserRepository userRepository,
                           TemplateRepository templateRepository,
                           ChatSessionRepository chatSessionRepository) {
        this.documentRepository = documentRepository;
        this.categoryService = categoryService;
        this.templateService = templateService;
        this.mapperService = mapperService;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.templateRepository = templateRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    public List<DocumentDtos.DocumentResponse> list(UUID categoryId, UUID templateId, SourceType sourceType, String search) {
        Predicate<DocumentRecord> filter = document -> true;
        if (categoryId != null) {
            filter = filter.and(document -> document.getCategory().getId().equals(categoryId));
        }
        if (templateId != null) {
            filter = filter.and(document -> document.getTemplate().getId().equals(templateId));
        }
        if (sourceType != null) {
            filter = filter.and(document -> document.getSourceType() == sourceType);
        }
        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();
            filter = filter.and(document ->
                    document.getTitle().toLowerCase().contains(lower) ||
                            (document.getContent() != null && document.getContent().toLowerCase().contains(lower)));
        }
        return documentRepository.findAll().stream()
                .filter(filter)
                .map(mapperService::toDocumentResponse)
                .toList();
    }

    public DocumentRecord findEntity(UUID id) {
        return documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Documento não encontrado"));
    }

    public List<DocumentRecord> findAllEntities() {
        return documentRepository.findAll();
    }

    public DocumentDtos.DocumentResponse get(UUID id) {
        return mapperService.toDocumentResponse(findEntity(id));
    }

    public DocumentDtos.DocumentResponse create(DocumentDtos.DocumentRequest request) {
        DocumentRecord document = new DocumentRecord();
        apply(document, request);
        return mapperService.toDocumentResponse(documentRepository.save(document));
    }

    public DocumentDtos.DocumentResponse update(UUID id, DocumentDtos.DocumentRequest request) {
        DocumentRecord document = findEntity(id);
        apply(document, request);
        return mapperService.toDocumentResponse(documentRepository.save(document));
    }

    public List<DocumentDtos.DocumentResponse> recentCreated() {
        return documentRepository.findTop5ByOrderByCreatedAtDesc().stream().map(mapperService::toDocumentResponse).toList();
    }

    public List<DocumentDtos.DocumentResponse> recentUpdated() {
        return documentRepository.findTop5ByOrderByUpdatedAtDesc().stream().map(mapperService::toDocumentResponse).toList();
    }

    public DocumentDtos.DocumentMetricsResponse metrics() {
        Map<SourceType, Long> bySource = Arrays.stream(SourceType.values())
                .collect(Collectors.toMap(sourceType -> sourceType, documentRepository::countBySourceType));
        return new DocumentDtos.DocumentMetricsResponse(
                categoryRepository.count(),
                userRepository.count(),
                templateRepository.count(),
                documentRepository.count(),
                chatSessionRepository.count(),
                bySource
        );
    }

    private void apply(DocumentRecord document, DocumentDtos.DocumentRequest request) {
        document.setTitle(request.title());
        document.setCategory(categoryService.findEntity(request.categoryId()));
        document.setTemplate(templateService.findEntity(request.templateId()));
        document.setSourceType(request.sourceType());
        document.setContent(request.content());
    }
}
