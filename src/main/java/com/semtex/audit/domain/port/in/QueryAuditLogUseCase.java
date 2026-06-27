package com.semtex.audit.domain.port.in;

import com.semtex.audit.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

/** Puerto de entrada (driving): consulta del historial de auditoría del tenant. */
public interface QueryAuditLogUseCase {

    List<AuditLog> listByOrganization(UUID organizationId, int limit);
}
