package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.UserDtos;
import com.aiprocess.backendonia.service.UserService;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDtos.UserResponse> list() {
        return userService.list();
    }

    @PostMapping
    public UserDtos.UserResponse create(@Valid @RequestBody UserDtos.UserRequest request) {
        return userService.create(request);
    }

    @PatchMapping("/{id}")
    public UserDtos.UserResponse update(@PathVariable UUID id, @Valid @RequestBody UserDtos.UserRequest request) {
        return userService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public UserDtos.UserResponse updateStatus(@PathVariable UUID id, @RequestBody UserDtos.UserStatusRequest request) {
        return userService.updateStatus(id, request.isActive());
    }
}
