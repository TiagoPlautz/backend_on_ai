package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.UserDtos;
import com.aiprocess.backendonia.service.MapperService;
import com.aiprocess.backendonia.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;
    private final MapperService mapperService;

    public AuthController(UserService userService, MapperService mapperService) {
        this.userService = userService;
        this.mapperService = mapperService;
    }

    @GetMapping("/me")
    public UserDtos.UserResponse me() {
        return mapperService.toUserResponse(userService.getDefaultUser());
    }
}
