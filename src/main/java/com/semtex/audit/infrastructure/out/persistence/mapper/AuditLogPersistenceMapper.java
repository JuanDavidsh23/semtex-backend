package com.semtex.audit.infrastructure.out.persistence.mapper;

import com.semtex.audit.domain.model.AuditLog;
import com.semtex.audit.infrastructure.out.persistence.entity.AuditLogJpaEntity;
import org.mapstruct.Mapper;

/** Mapper MapStruct entre el dominio AuditLog y su entidad JPA (AuditAction ↔ AuditActionJpa). */
@Mapper
public interface AuditLogPersistenceMapper {

    AuditLogJpaEntity toEntity(AuditLog domain);

    AuditLog toDomain(AuditLogJpaEntity entity);
}
