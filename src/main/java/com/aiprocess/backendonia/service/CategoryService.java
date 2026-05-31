package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.api.dto.CategoryDtos;
import com.aiprocess.backendonia.domain.Category;
import com.aiprocess.backendonia.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MapperService mapperService;

    public CategoryService(CategoryRepository categoryRepository, MapperService mapperService) {
        this.categoryRepository = categoryRepository;
        this.mapperService = mapperService;
    }

    public List<CategoryDtos.CategoryResponse> list() {
        return categoryRepository.findAll().stream().map(mapperService::toCategoryResponse).toList();
    }

    public Category findEntity(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
    }

    public CategoryDtos.CategoryResponse create(CategoryDtos.CategoryRequest request) {
        Category category = new Category();
        apply(category, request);
        return mapperService.toCategoryResponse(categoryRepository.save(category));
    }

    public CategoryDtos.CategoryResponse update(UUID id, CategoryDtos.CategoryRequest request) {
        Category category = findEntity(id);
        apply(category, request);
        return mapperService.toCategoryResponse(categoryRepository.save(category));
    }

    public CategoryDtos.CategoryResponse updateStatus(UUID id, boolean active) {
        Category category = findEntity(id);
        category.setActive(active);
        return mapperService.toCategoryResponse(categoryRepository.save(category));
    }

    private void apply(Category category, CategoryDtos.CategoryRequest request) {
        category.setName(request.name());
        category.setSector(request.sector());
        category.setSummary(request.summary());
    }
}
