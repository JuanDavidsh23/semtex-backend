package com.semtex.chat.infrastructure.in.web.dto;

import com.semtex.financial.infrastructure.in.web.dto.FinancialRecordDtos.RecordResponse;
import jakarta.validation.constraints.NotBlank;

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
}
