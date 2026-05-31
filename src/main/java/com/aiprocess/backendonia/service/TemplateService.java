package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.api.dto.TemplateDtos;
import com.aiprocess.backendonia.domain.TemplateEntity;
import com.aiprocess.backendonia.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final MapperService mapperService;

    public TemplateService(TemplateRepository templateRepository, MapperService mapperService) {
        this.templateRepository = templateRepository;
        this.mapperService = mapperService;
    }

    public List<TemplateDtos.TemplateResponse> list() {
        return templateRepository.findAll().stream().map(mapperService::toTemplateResponse).toList();
    }

    public TemplateEntity findEntity(UUID id) {
        return templateRepository.findById(id).orElseThrow(() -> new NotFoundException("Modelo não encontrado"));
    }

    public TemplateEntity firstAvailable() {
        return templateRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Nenhum modelo cadastrado. Crie um template antes de usar o chat no modo CREATE."));
    }

    public TemplateDtos.TemplateResponse create(TemplateDtos.TemplateRequest request) {
        TemplateEntity template = new TemplateEntity();
        apply(template, request);
        return mapperService.toTemplateResponse(templateRepository.save(template));
    }

    public TemplateDtos.TemplateResponse update(UUID id, TemplateDtos.TemplateRequest request) {
        TemplateEntity template = findEntity(id);
        apply(template, request);
        return mapperService.toTemplateResponse(templateRepository.save(template));
    }

    public void delete(UUID id) {
        templateRepository.delete(findEntity(id));
    }

    private void apply(TemplateEntity template, TemplateDtos.TemplateRequest request) {
        template.setName(request.name());
        template.setFileType(request.fileType());
        template.setScope(request.scope());
    }
}
