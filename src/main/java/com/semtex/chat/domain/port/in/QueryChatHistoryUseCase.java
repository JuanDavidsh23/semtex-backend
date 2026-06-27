package com.semtex.chat.domain.port.in;

import com.semtex.chat.domain.model.ChatMessage;

import java.util.List;
import java.util.UUID;

/** Puerto de entrada (driving): historial de conversación del usuario. */
public interface QueryChatHistoryUseCase {

    List<ChatMessage> history(UUID organizationId, UUID userId, UUID documentId, int limit);
}
