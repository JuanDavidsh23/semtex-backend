package com.semtex.audit.infrastructure.in.web.dto;

import com.semtex.audit.domain.model.AuditAction;
import com.semtex.audit.domain.model.AuditLog;

import java.time.LocalDateTime;
import java.util.UUID;

/** DTOs REST del contexto audit. */
public final class AuditLogDtos {

    private AuditLogDtos() {}

    public record Response(
            UUID id, AuditAction action, String description, UUID performedBy, LocalDateTime createdAt
    ) {
        public static Response from(AuditLog log) {
            return new Response(log.getId(), log.getAction(), log.getDescription(),
                    log.getPerformedBy(), log.getCreatedAt());
        }
    }
}
