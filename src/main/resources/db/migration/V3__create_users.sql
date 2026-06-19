-- ============================================================
-- V3: Tabla users con RBAC. El id se sincroniza con el subject del JWT.
-- ============================================================

CREATE TABLE users (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    role            user_role    NOT NULL DEFAULT 'OPERATOR',
    organization_id UUID         NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_organization ON users(organization_id);
CREATE INDEX idx_users_email        ON users(email);
CREATE INDEX idx_users_role         ON users(organization_id, role);

COMMENT ON TABLE users IS 'Usuarios del sistema. El id/email coincide con el subject/email del JWT.';
COMMENT ON COLUMN users.role IS 'ADMIN: control total. OPERATOR: operaciones diarias. AUDITOR: solo lectura.';
