package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.ChatDtos;
import com.aiprocess.backendonia.domain.ChatMode;
import com.aiprocess.backendonia.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat/sessions")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<ChatDtos.ChatSessionResponse> list(@RequestParam ChatMode mode) {
        return chatService.list(mode);
    }

    @PostMapping
    public ChatDtos.ChatSessionResponse createSession(@Valid @RequestBody ChatDtos.ChatSessionRequest request) {
        return chatService.createSession(request);
    }

    @GetMapping("/{id}/messages")
    public List<ChatDtos.ChatMessageResponse> messages(@PathVariable UUID id) {
        return chatService.messages(id);
    }

    @PostMapping("/{id}/messages")
    public ChatDtos.ChatReplyResponse reply(@PathVariable UUID id, @Valid @RequestBody ChatDtos.ChatMessageRequest request) {
        return chatService.reply(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        chatService.deleteSession(id);
    }
}
