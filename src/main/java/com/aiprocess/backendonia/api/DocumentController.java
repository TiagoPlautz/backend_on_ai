package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.DocumentDtos;
import com.aiprocess.backendonia.domain.SourceType;
import com.aiprocess.backendonia.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<DocumentDtos.DocumentResponse> list(@RequestParam(required = false) UUID categoryId,
                                                    @RequestParam(required = false) UUID templateId,
                                                    @RequestParam(required = false) SourceType sourceType,
                                                    @RequestParam(required = false) String search) {
        return documentService.list(categoryId, templateId, sourceType, search);
    }

    @GetMapping("/{id}")
    public DocumentDtos.DocumentResponse get(@PathVariable UUID id) {
        return documentService.get(id);
    }

    @PostMapping
    public DocumentDtos.DocumentResponse create(@Valid @RequestBody DocumentDtos.DocumentRequest request) {
        return documentService.create(request);
    }

    @PatchMapping("/{id}")
    public DocumentDtos.DocumentResponse update(@PathVariable UUID id, @Valid @RequestBody DocumentDtos.DocumentRequest request) {
        return documentService.update(id, request);
    }

    @GetMapping("/recent-created")
    public List<DocumentDtos.DocumentResponse> recentCreated() {
        return documentService.recentCreated();
    }

    @GetMapping("/recent-updated")
    public List<DocumentDtos.DocumentResponse> recentUpdated() {
        return documentService.recentUpdated();
    }

    @GetMapping("/metrics")
    public DocumentDtos.DocumentMetricsResponse metrics() {
        return documentService.metrics();
    }
}
