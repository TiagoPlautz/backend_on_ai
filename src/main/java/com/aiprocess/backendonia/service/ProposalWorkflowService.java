package com.aiprocess.backendonia.service;

import com.aiprocess.backendonia.ai.DocumentationAssistant;
import com.aiprocess.backendonia.api.dto.CategoryDtos;
import com.aiprocess.backendonia.api.dto.DocumentDtos;
import com.aiprocess.backendonia.api.dto.TemplateDtos;
import com.aiprocess.backendonia.api.dto.WorkflowDtos;
import com.aiprocess.backendonia.domain.DocumentRecord;
import com.aiprocess.backendonia.domain.ProposalRecord;
import com.aiprocess.backendonia.domain.ProposalStatus;
import com.aiprocess.backendonia.domain.SourceType;
import com.aiprocess.backendonia.repository.ProposalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ProposalWorkflowService {

    private final DocumentationAssistant assistant;
    private final DocumentService documentService;
    private final ProposalRepository proposalRepository;
    private final KnowledgeBaseService knowledgeBaseService;
    private final TemplateService templateService;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    public ProposalWorkflowService(DocumentationAssistant assistant,
                                   DocumentService documentService,
                                   ProposalRepository proposalRepository,
                                   KnowledgeBaseService knowledgeBaseService,
                                   TemplateService templateService,
                                   CategoryService categoryService,
                                   ObjectMapper objectMapper) {
        this.assistant = assistant;
        this.documentService = documentService;
        this.proposalRepository = proposalRepository;
        this.knowledgeBaseService = knowledgeBaseService;
        this.templateService = templateService;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public WorkflowDtos.ChatResponse chat(WorkflowDtos.ChatRequest request) {
        String prompt = buildPrompt(request);
        String rawResponse;
        try {
            rawResponse = assistant.answer(prompt);
        } catch (Exception exception) {
            throw new AiIntegrationException(
                    "Falha ao consultar a Anthropic para gerar a proposta. Verifique a configuração da IA no backend.",
                    exception
            );
        }

        AiProposalPayload payload = parsePayload(rawResponse);
        if ("proposal".equalsIgnoreCase(payload.type())) {
            if (payload.title() == null || payload.title().isBlank() || payload.content() == null || payload.content().isBlank()) {
                throw new AiIntegrationException(
                        "A IA indicou uma proposta, mas não devolveu título e conteúdo suficientes para revisão.",
                        null
                );
            }
            ProposalRecord proposal = new ProposalRecord();
            proposal.setTitle(payload.title());
            proposal.setContent(payload.content());
            proposal.setSummary(payload.summary());
            proposal.setStatus(ProposalStatus.PENDING_REVIEW);
            proposal.setSourcePrompt(prompt);
            proposal = proposalRepository.save(proposal);

            return new WorkflowDtos.ChatResponse(
                    "proposal",
                    payload.message() != null && !payload.message().isBlank()
                            ? payload.message()
                            : "Proposta criada para revisão.",
                    toProposalDocumentResponse(proposal)
            );
        }

        return new WorkflowDtos.ChatResponse(
                "message",
                payload.message() != null && !payload.message().isBlank()
                        ? payload.message()
                        : "A IA respondeu sem gerar proposta.",
                null
        );
    }

    @Transactional
    public WorkflowDtos.ApproveResponse approve(WorkflowDtos.ApproveRequest request) {
        ProposalRecord proposal = proposalRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("Proposta não encontrada"));

        if (proposal.getStatus() == ProposalStatus.APPROVED) {
            return new WorkflowDtos.ApproveResponse(
                    "approved",
                    "A proposta já havia sido aprovada anteriormente.",
                    toProposalDocumentResponse(proposal),
                    proposal.getGeneratedFileName(),
                    true
            );
        }

        String action = request.acao().trim().toUpperCase();
        if ("REJECT".equals(action) || "REJEITAR".equals(action)) {
            proposal.setStatus(ProposalStatus.REJECTED);
            proposalRepository.save(proposal);
            return new WorkflowDtos.ApproveResponse(
                    "rejected",
                    "Proposta rejeitada. Nenhum arquivo foi gravado na base.",
                    toProposalDocumentResponse(proposal),
                    null,
                    false
            );
        }

        if (!"APPROVE".equals(action) && !"APROVAR".equals(action)) {
            throw new IllegalArgumentException("Ação inválida. Use APPROVE/APROVAR ou REJECT/REJEITAR.");
        }

        String contentToPersist = request.conteudo() != null && !request.conteudo().isBlank()
                ? request.conteudo()
                : proposal.getContent();

        Path filePath = knowledgeBaseService.approveProposal(proposal, contentToPersist);
        proposal.setContent(contentToPersist);
        proposal.setStatus(ProposalStatus.APPROVED);
        proposal.setGeneratedFileName(filePath.getFileName().toString());
        proposalRepository.save(proposal);

        persistAsDocumentIfPossible(proposal);

        return new WorkflowDtos.ApproveResponse(
                "approved",
                "Documento salvo na base de conhecimento com sucesso.",
                toProposalDocumentResponse(proposal),
                filePath.toString(),
                true
        );
    }

    private void persistAsDocumentIfPossible(ProposalRecord proposal) {
        CategoryDtos.CategoryResponse category = ensureCategoryForKnowledgeBase();
        TemplateDtos.TemplateResponse template = ensureTemplateForKnowledgeBase();

        boolean alreadyExists = documentService.list(null, null, SourceType.CHAT, proposal.getTitle()).stream()
                .anyMatch(document -> document.title().equalsIgnoreCase(proposal.getTitle()));
        if (alreadyExists) {
            return;
        }

        documentService.create(new DocumentDtos.DocumentRequest(
                proposal.getTitle(),
                category.id(),
                template.id(),
                SourceType.CHAT,
                proposal.getContent()
        ));
    }

    private CategoryDtos.CategoryResponse ensureCategoryForKnowledgeBase() {
        List<CategoryDtos.CategoryResponse> categories = categoryService.list();
        if (!categories.isEmpty()) {
            return categories.get(0);
        }

        return categoryService.create(new CategoryDtos.CategoryRequest(
                "Knowledge Base",
                "Interno",
                "Categoria padrão criada automaticamente para persistir documentos aprovados pela IA."
        ));
    }

    private TemplateDtos.TemplateResponse ensureTemplateForKnowledgeBase() {
        List<TemplateDtos.TemplateResponse> templates = templateService.list();
        if (!templates.isEmpty()) {
            return templates.get(0);
        }

        return templateService.create(new TemplateDtos.TemplateRequest(
                "Template Padrão KB",
                "md",
                "Template padrão criado automaticamente para documentos aprovados pela IA."
        ));
    }

    private String buildPrompt(WorkflowDtos.ChatRequest request) {
        String conversation = request.messages().stream()
                .map(message -> message.role().toUpperCase() + ": " + message.content())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");

        String attachment = "";
        if (request.attachmentContent() != null && !request.attachmentContent().isBlank()) {
            attachment = """

                    Conteúdo enviado como arquivo/texto complementar:
                    Nome: %s
                    Conteúdo:
                    %s
                    """.formatted(
                    request.attachmentName() != null ? request.attachmentName() : "anexo",
                    request.attachmentContent()
            );
        }

        return """
                Você é o core de uma plataforma de documentação corporativa.
                Seu trabalho é agir como o produto principal e decidir se deve responder normalmente ou propor um documento para revisão.

                Antes de propor um documento:
                - Considere a base existente recebida abaixo como resultado interno de listar_documentos e ler_documento.
                - Evite duplicar conteúdos já existentes.
                - Quando existir material suficiente, produza uma proposta bem estruturada.

                Retorne APENAS JSON válido no formato:
                {
                  "type": "proposal" | "message",
                  "message": "texto curto para o chat",
                  "title": "titulo da proposta se houver",
                  "summary": "resumo curto da proposta se houver",
                  "content": "conteudo completo em markdown se houver"
                }

                Use "proposal" quando o usuário estiver pedindo criação/atualização de documentação pronta para revisão.
                Use "message" quando ainda faltar contexto ou quando a melhor saída for só responder.

                Base atual:
                %s

                Conversa:
                %s
                %s
                """.formatted(buildKnowledgeBaseContext(), conversation, attachment);
    }

    private String buildKnowledgeBaseContext() {
        List<DocumentRecord> documents = documentService.findAllEntities();
        if (documents.isEmpty()) {
            return "Nenhum documento cadastrado na base no momento.";
        }

        return documents.stream()
                .limit(8)
                .map(document -> """
                        - ID: %s
                          Título: %s
                          Conteúdo: %s
                        """.formatted(
                        document.getId(),
                        document.getTitle(),
                        trimContent(document.getContent())
                ))
                .reduce((left, right) -> left + "\n" + right)
                .orElse("Nenhum documento cadastrado na base no momento.");
    }

    private String trimContent(String content) {
        if (content == null) {
            return "";
        }
        return content.length() > 1000 ? content.substring(0, 1000) + "..." : content;
    }

    private AiProposalPayload parsePayload(String rawResponse) {
        String sanitized = rawResponse == null ? "" : rawResponse.trim();
        if (sanitized.startsWith("```")) {
            sanitized = sanitized.replaceFirst("^```json\\s*", "")
                    .replaceFirst("^```\\s*", "")
                    .replaceFirst("\\s*```$", "");
        }

        try {
            return objectMapper.readValue(sanitized, AiProposalPayload.class);
        } catch (Exception exception) {
            throw new AiIntegrationException(
                    "A IA respondeu em um formato inesperado. Verifique o prompt e os logs do backend.",
                    exception
            );
        }
    }

    private WorkflowDtos.ProposalDocumentResponse toProposalDocumentResponse(ProposalRecord proposal) {
        return new WorkflowDtos.ProposalDocumentResponse(
                proposal.getId(),
                proposal.getTitle(),
                proposal.getContent(),
                proposal.getSummary(),
                proposal.getStatus(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt()
        );
    }

    private record AiProposalPayload(
            String type,
            String message,
            String title,
            String summary,
            String content
    ) {
    }
}
