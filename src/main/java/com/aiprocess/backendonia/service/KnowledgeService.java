package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.api.dto.DocumentDtos;
import com.aiprocess.backendonia.api.dto.KnowledgeDtos;
import com.aiprocess.backendonia.domain.SourceType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeService {

    private final DocumentService documentService;

    public KnowledgeService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public KnowledgeDtos.UploadResponse uploadFiles(List<MultipartFile> files, UUID categoryId, UUID templateId) {
        List<DocumentDtos.DocumentResponse> documents = files.stream()
                .map(file -> documentService.create(new DocumentDtos.DocumentRequest(
                        file.getOriginalFilename() != null ? file.getOriginalFilename() : "arquivo-importado",
                        categoryId,
                        templateId,
                        SourceType.FILE,
                        "Arquivo recebido pelo backend: " + file.getOriginalFilename()
                )))
                .toList();
        return new KnowledgeDtos.UploadResponse(documents);
    }

    public DocumentDtos.DocumentResponse ingestUrl(KnowledgeDtos.UrlIngestionRequest request) {
        return documentService.create(new DocumentDtos.DocumentRequest(
                request.title(),
                request.categoryId(),
                request.templateId(),
                SourceType.URL,
                "URL cadastrada para ingestão: " + request.url()
        ));
    }

    public KnowledgeDtos.AudioTranscriptionResponse transcribe(MultipartFile file, UUID categoryId, UUID templateId) {
        String transcription = "Transcrição simulada do arquivo: " + file.getOriginalFilename();
        DocumentDtos.DocumentResponse document = documentService.create(new DocumentDtos.DocumentRequest(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio-importado",
                categoryId,
                templateId,
                SourceType.AUDIO,
                transcription
        ));
        return new KnowledgeDtos.AudioTranscriptionResponse(transcription, document);
    }
}
