package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.TemplateDtos;
import com.aiprocess.backendonia.service.TemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<TemplateDtos.TemplateResponse> list() {
        return templateService.list();
    }

    @PostMapping
    public TemplateDtos.TemplateResponse create(@RequestHeader(value = "X-Admin", defaultValue = "false") boolean isAdmin,
                                                @Valid @RequestBody TemplateDtos.TemplateRequest request) {
        validateAdmin(isAdmin);
        return templateService.create(request);
    }

    @PatchMapping("/{id}")
    public TemplateDtos.TemplateResponse update(@PathVariable UUID id,
                                                @RequestHeader(value = "X-Admin", defaultValue = "false") boolean isAdmin,
                                                @Valid @RequestBody TemplateDtos.TemplateRequest request) {
        validateAdmin(isAdmin);
        return templateService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @RequestHeader(value = "X-Admin", defaultValue = "false") boolean isAdmin) {
        validateAdmin(isAdmin);
        templateService.delete(id);
    }

    private void validateAdmin(boolean isAdmin) {
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operação permitida apenas para administradores");
        }
    }
}
