package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.HomeDtos;
import com.aiprocess.backendonia.service.DocumentService;
import com.aiprocess.backendonia.service.MapperService;
import com.aiprocess.backendonia.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class HomeController {

    private final UserService userService;
    private final DocumentService documentService;
    private final MapperService mapperService;

    public HomeController(UserService userService, DocumentService documentService, MapperService mapperService) {
        this.userService = userService;
        this.documentService = documentService;
        this.mapperService = mapperService;
    }

    @GetMapping("/home")
    public HomeDtos.HomeResponse home() {
        return new HomeDtos.HomeResponse(
                mapperService.toUserResponse(userService.getDefaultUser()),
                documentService.metrics(),
                documentService.recentCreated(),
                documentService.recentUpdated()
        );
    }
}
