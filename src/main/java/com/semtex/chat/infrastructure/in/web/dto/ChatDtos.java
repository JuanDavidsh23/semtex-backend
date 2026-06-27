package com.semtex.chat.infrastructure.in.web.dto;

import com.semtex.chat.domain.model.ChatMessage;
import com.semtex.chat.domain.model.MessageRole;
import com.semtex.financial.infrastructure.in.web.dto.FinancialRecordDtos.RecordResponse;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** DTOs REST del contexto chat. */
public final class ChatDtos {

    private ChatDtos() {}

    public record SendRequest(
            @NotBlank(message = "El contenido del mensaje es obligatorio") String content,
            UUID documentId
    ) {}

    public record SendResponse(
            String agentResponse,
            List<RecordResponse> relevantRecords
    ) {}

    public record MessageResponse(
            UUID id, MessageRole role, String content, UUID userId, UUID documentId,
            Integer tokensUsed, LocalDateTime createdAt
    ) {
        public static MessageResponse from(ChatMessage m) {
            return new MessageResponse(m.getId(), m.getRole(), m.getContent(), m.getUserId(),
                    m.getDocumentId(), m.getTokensUsed(), m.getCreatedAt());
        }
    }
}
