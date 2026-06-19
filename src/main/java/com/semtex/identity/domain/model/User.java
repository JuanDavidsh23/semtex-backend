package com.semtex.identity.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio: User — pertenece a exactamente una organización (multi-tenant estricto).
 * El id se sincroniza con el subject del JWT de autenticación.
 *
 * REGLA HEXAGONAL: Java puro, sin anotaciones externas.
 */
public class User {

    private final UUID id;
    private String email;
    private Role role;
    private final UUID organizationId;
    private boolean active;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Crea un usuario nuevo. */
    public User(String email, Role role, UUID organizationId) {
        this(UUID.randomUUID(), email, role, organizationId, true, null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    /** Reconstruye desde persistencia. */
    public User(UUID id, String email, Role role, UUID organizationId,
                boolean active, LocalDateTime lastLoginAt,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.organizationId = organizationId;
        this.active = active;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeRole(Role newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo.");
        }
        this.role = newRole;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId()                   { return id; }
    public String getEmail()              { return email; }
    public Role getRole()                 { return role; }
    public UUID getOrganizationId()       { return organizationId; }
    public boolean isActive()             { return active; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }
}
