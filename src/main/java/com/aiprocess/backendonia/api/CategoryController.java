package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.CategoryDtos;
import com.aiprocess.backendonia.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDtos.CategoryResponse> list() {
        return categoryService.list();
    }

    @PostMapping
    public CategoryDtos.CategoryResponse create(@Valid @RequestBody CategoryDtos.CategoryRequest request) {
        return categoryService.create(request);
    }

    @PatchMapping("/{id}")
    public CategoryDtos.CategoryResponse update(@PathVariable UUID id, @Valid @RequestBody CategoryDtos.CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public CategoryDtos.CategoryResponse updateStatus(@PathVariable UUID id, @RequestBody CategoryDtos.CategoryStatusRequest request) {
        return categoryService.updateStatus(id, request.isActive());
    }
}
