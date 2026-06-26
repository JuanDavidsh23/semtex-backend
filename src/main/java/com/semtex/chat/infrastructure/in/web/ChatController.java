package com.semtex.chat.infrastructure.in.web;

import com.semtex.chat.domain.port.in.ChatUseCase;
import com.semtex.chat.domain.port.in.ChatUseCase.ChatResult;
import com.semtex.chat.domain.port.in.ChatUseCase.SendMessageCommand;
import com.semtex.chat.infrastructure.in.web.dto.ChatDtos.SendRequest;
import com.semtex.chat.infrastructure.in.web.dto.ChatDtos.SendResponse;
import com.semtex.financial.infrastructure.in.web.dto.FinancialRecordDtos.RecordResponse;
import com.semtex.shared.tenant.TenantContext;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatUseCase chatUseCase;

    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping("/messages")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public SendResponse send(@Valid @RequestBody SendRequest request) {
        UUID organizationId = TenantContext.requireOrganizationId();
        UUID userId = TenantContext.currentUserId();
        ChatResult result = chatUseCase.sendMessage(
                new SendMessageCommand(request.content(), request.documentId(), organizationId, userId));
        return new SendResponse(
                result.agentResponse(),
                result.relevantRecords().stream().map(RecordResponse::from).toList());
    }
}
