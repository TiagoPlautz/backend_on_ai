package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.ai.DocumentationAssistant;
import com.aiprocess.backendonia.api.dto.ChatDtos;
import com.aiprocess.backendonia.domain.ChatMessage;
import com.aiprocess.backendonia.domain.ChatMode;
import com.aiprocess.backendonia.domain.ChatRole;
import com.aiprocess.backendonia.domain.ChatSession;
import com.aiprocess.backendonia.domain.DocumentRecord;
import com.aiprocess.backendonia.domain.SourceType;
import com.aiprocess.backendonia.repository.ChatMessageRepository;
import com.aiprocess.backendonia.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserService userService;
    private final DocumentService documentService;
    private final TemplateService templateService;
    private final MapperService mapperService;
    private final DocumentationAssistant assistant;

    public ChatService(ChatSessionRepository sessionRepository,
                       ChatMessageRepository messageRepository,
                       UserService userService,
                       DocumentService documentService,
                       TemplateService templateService,
                       MapperService mapperService,
                       DocumentationAssistant assistant) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.documentService = documentService;
        this.templateService = templateService;
        this.mapperService = mapperService;
        this.assistant = assistant;
    }

    public List<ChatDtos.ChatSessionResponse> list(ChatMode mode) {
        return sessionRepository.findByUserAndModeOrderByUpdatedAtDesc(userService.getDefaultUser(), mode).stream()
                .map(mapperService::toChatSessionResponse)
                .toList();
    }

    public List<ChatDtos.ChatMessageResponse> messages(UUID sessionId) {
        ChatSession session = findSession(sessionId);
        return messageRepository.findBySessionOrderByCreatedAtAsc(session).stream()
                .map(mapperService::toChatMessageResponse)
                .toList();
    }

    public ChatDtos.ChatSessionResponse createSession(ChatDtos.ChatSessionRequest request) {
        ChatSession session = new ChatSession();
        session.setMode(request.mode());
        session.setTitle(request.title() != null && !request.title().isBlank() ? request.title() : defaultTitle(request.mode()));
        session.setUser(userService.getDefaultUser());
        if (request.documentId() != null) {
            session.setDocument(documentService.findEntity(request.documentId()));
        }
        return mapperService.toChatSessionResponse(sessionRepository.save(session));
    }

    @Transactional
    public ChatDtos.ChatReplyResponse reply(UUID sessionId, ChatDtos.ChatMessageRequest request) {
        ChatSession session = findSession(sessionId);
        ChatMessage userMessage = saveMessage(session, ChatRole.USER, request.content());
        String answer;
        try {
            answer = assistant.answer(buildPrompt(session, request.content()));
        } catch (Exception exception) {
            throw new AiIntegrationException(
                    "Falha ao consultar a Anthropic. Verifique a API key, o modelo configurado e os logs do backend.",
                    exception
            );
        }
        ChatMessage assistantMessage = saveMessage(session, ChatRole.ASSISTANT, answer);

        if (session.getMode() == ChatMode.CREATE) {
            persistChatDocument(session, answer);
        } else if (session.getMode() == ChatMode.UPDATE && session.getDocument() != null) {
            DocumentRecord document = session.getDocument();
            document.setContent(answer);
        }

        sessionRepository.save(session);
        return new ChatDtos.ChatReplyResponse(
                mapperService.toChatSessionResponse(session),
                List.of(
                        mapperService.toChatMessageResponse(userMessage),
                        mapperService.toChatMessageResponse(assistantMessage)
                )
        );
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        ChatSession session = findSession(sessionId);
        messageRepository.deleteBySession(session);
        sessionRepository.delete(session);
    }

    private ChatSession findSession(UUID sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new NotFoundException("Sessão não encontrada"));
    }

    private ChatMessage saveMessage(ChatSession session, ChatRole role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setRole(role);
        message.setContent(content);
        return messageRepository.save(message);
    }

    private String buildPrompt(ChatSession session, String content) {
        String context = switch (session.getMode()) {
            case CREATE -> """
                    Você está no fluxo CREATE.
                    Gere documentação nova, estruturada e pronta para base de conhecimento.
                    """;
            case UPDATE -> """
                    Você está no fluxo UPDATE.
                    Atualize a documentação existente preservando clareza, contexto e consistência.
                    """;
            case CONSULT -> """
                    Você está no fluxo CONSULT.
                    Responda perguntas sobre a base de conhecimento de forma objetiva.
                    """;
        };

        String documentContext = "";
        if (session.getDocument() != null) {
            DocumentRecord document = session.getDocument();
            documentContext = """
                    Documento selecionado:
                    Título: %s
                    Conteúdo atual:
                    %s
                    """.formatted(document.getTitle(), document.getContent());
        }

        return """
                Você é a IA do sistema de documentação interno.
                %s

                Regras:
                - Responda em português do Brasil.
                - Use linguagem corporativa clara.
                - Quando fizer sentido, devolva seções e bullets práticos.
                - Se a pergunta não tiver contexto suficiente, assuma o mínimo e sinalize a suposição.

                %s

                Mensagem do usuário:
                %s
                """.formatted(context, documentContext, content);
    }

    private void persistChatDocument(ChatSession session, String answer) {
        if (session.getDocument() != null) {
            return;
        }
        session.setDocument(documentService.findEntity(documentService.create(
                new com.aiprocess.backendonia.api.dto.DocumentDtos.DocumentRequest(
                        session.getTitle(),
                        userService.getDefaultUser().getCategory().getId(),
                        templateService.firstAvailable().getId(),
                        SourceType.CHAT,
                        answer
                )
        ).id()));
    }

    private String defaultTitle(ChatMode mode) {
        return switch (mode) {
            case CREATE -> "Nova documentação";
            case UPDATE -> "Atualização de documentação";
            case CONSULT -> "Consulta na base";
        };
    }
}
