package com.aiprocess.backendonia.api;

import com.aiprocess.backendonia.api.dto.WorkflowDtos;
import com.aiprocess.backendonia.service.ProposalWorkflowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WorkflowController {

    private final ProposalWorkflowService proposalWorkflowService;

    public WorkflowController(ProposalWorkflowService proposalWorkflowService) {
        this.proposalWorkflowService = proposalWorkflowService;
    }

    @PostMapping("/chat")
    public WorkflowDtos.ChatResponse chat(@Valid @RequestBody WorkflowDtos.ChatRequest request) {
        return proposalWorkflowService.chat(request);
    }

    @PostMapping("/approve")
    public WorkflowDtos.ApproveResponse approve(@Valid @RequestBody WorkflowDtos.ApproveRequest request) {
        return proposalWorkflowService.approve(request);
    }
}
