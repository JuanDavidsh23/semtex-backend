package com.semtex.identity.domain.port.in;

import com.semtex.identity.domain.model.Role;
import com.semtex.identity.domain.model.User;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada (driving): gestión de usuarios y RBAC.
 * El {@code organizationId} llega ya resuelto del token (lo inyecta el controller desde TenantContext).
 */
public interface ManageUserUseCase {

    User create(CreateUserCommand command);

    User changeRole(UUID userId, Role newRole);

    void deactivate(UUID userId);

    User get(UUID userId);

    List<User> listByOrganization(UUID organizationId);

    record CreateUserCommand(String email, Role role, UUID organizationId) {}
}
