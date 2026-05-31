package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.api.dto.UserDtos;
import com.aiprocess.backendonia.domain.AppUser;
import com.aiprocess.backendonia.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final CategoryService categoryService;
    private final MapperService mapperService;

    public UserService(AppUserRepository userRepository, CategoryService categoryService, MapperService mapperService) {
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.mapperService = mapperService;
    }

    public List<UserDtos.UserResponse> list() {
        return userRepository.findAllByOrderByNameAsc().stream().map(mapperService::toUserResponse).toList();
    }

    public AppUser findEntity(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public AppUser getDefaultUser() {
        return userRepository.findAllByOrderByNameAsc().stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Nenhum usuário cadastrado. Crie um usuário pelo frontend antes de iniciar o chat."));
    }

    public UserDtos.UserResponse create(UserDtos.UserRequest request) {
        AppUser user = new AppUser();
        apply(user, request);
        return mapperService.toUserResponse(userRepository.save(user));
    }

    public UserDtos.UserResponse update(UUID id, UserDtos.UserRequest request) {
        AppUser user = findEntity(id);
        apply(user, request);
        return mapperService.toUserResponse(userRepository.save(user));
    }

    public UserDtos.UserResponse updateStatus(UUID id, boolean active) {
        AppUser user = findEntity(id);
        user.setActive(active);
        return mapperService.toUserResponse(userRepository.save(user));
    }

    private void apply(AppUser user, UserDtos.UserRequest request) {
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setInitials(request.initials() != null && !request.initials().isBlank() ? request.initials() : buildInitials(request.name()));
        user.setCategory(categoryService.findEntity(request.categoryId()));
    }

    private String buildInitials(String name) {
        return java.util.Arrays.stream(name.trim().split("\\s+"))
                .filter(part -> !part.isBlank())
                .limit(2)
                .map(part -> part.substring(0, 1).toUpperCase())
                .reduce("", String::concat);
    }
}
