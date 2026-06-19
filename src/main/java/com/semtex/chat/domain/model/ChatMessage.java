package com.semtex.chat.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio: ChatMessage — un mensaje en la conversación con el agente.
 *
 * REGLA HEXAGONAL: Java puro, sin anotaciones externas.
 */
public class ChatMessage {

    private final UUID id;
    private final MessageRole role;
    private final String content;
    private final UUID organizationId;
    private final UUID userId;
    private final UUID documentId;   // null si el chat no está asociado a un documento
    private final Integer tokensUsed;
    private final LocalDateTime createdAt;

    /** Crea un mensaje nuevo. */
    public ChatMessage(MessageRole role, String content, UUID organizationId,
                       UUID userId, UUID documentId, Integer tokensUsed) {
        this(UUID.randomUUID(), role, content, organizationId, userId, documentId,
                tokensUsed, LocalDateTime.now());
    }

    /** Reconstruye desde persistencia. */
    public ChatMessage(UUID id, MessageRole role, String content, UUID organizationId,
                       UUID userId, UUID documentId, Integer tokensUsed, LocalDateTime createdAt) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.organizationId = organizationId;
        this.userId = userId;
        this.documentId = documentId;
        this.tokensUsed = tokensUsed;
        this.createdAt = createdAt;
    }

    public static ChatMessage user(String content, UUID organizationId, UUID userId, UUID documentId) {
        return new ChatMessage(MessageRole.USER, content, organizationId, userId, documentId, null);
    }

    public static ChatMessage agent(String content, UUID organizationId, UUID userId,
                                    UUID documentId, Integer tokensUsed) {
        return new ChatMessage(MessageRole.AGENT, content, organizationId, userId, documentId, tokensUsed);
    }

    public boolean isFromUser()  { return role == MessageRole.USER; }
    public boolean isFromAgent() { return role == MessageRole.AGENT; }

    public UUID getId()                 { return id; }
    public MessageRole getRole()        { return role; }
    public String getContent()          { return content; }
    public UUID getOrganizationId()     { return organizationId; }
    public UUID getUserId()             { return userId; }
    public UUID getDocumentId()         { return documentId; }
    public Integer getTokensUsed()      { return tokensUsed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
