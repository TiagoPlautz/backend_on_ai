package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.DocumentDtos;
import com.aiprocess.backendonia.api.dto.KnowledgeDtos;
import com.aiprocess.backendonia.service.KnowledgeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostMapping(path = "/uploads/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public KnowledgeDtos.UploadResponse uploadFiles(@RequestPart("files") List<MultipartFile> files,
                                                    @RequestParam UUID categoryId,
                                                    @RequestParam UUID templateId) {
        return knowledgeService.uploadFiles(files, categoryId, templateId);
    }

    @PostMapping("/knowledge/urls")
    public DocumentDtos.DocumentResponse ingestUrl(@Valid @org.springframework.web.bind.annotation.RequestBody KnowledgeDtos.UrlIngestionRequest request) {
        return knowledgeService.ingestUrl(request);
    }

    @PostMapping(path = "/knowledge/audio/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public KnowledgeDtos.AudioTranscriptionResponse transcribe(@RequestPart("file") MultipartFile file,
                                                               @RequestParam UUID categoryId,
                                                               @RequestParam UUID templateId) {
        return knowledgeService.transcribe(file, categoryId, templateId);
    }
}
