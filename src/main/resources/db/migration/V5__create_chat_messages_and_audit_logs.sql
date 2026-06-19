-- ============================================================
-- V5: chat_messages y audit_logs.
-- ============================================================

CREATE TABLE chat_messages (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    role            message_role NOT NULL,
    content         TEXT         NOT NULL,
    organization_id UUID         NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id         UUID         NOT NULL REFERENCES users(id),
    document_id     UUID         REFERENCES documents(id),
    tokens_used     INTEGER,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_messages_organization ON chat_messages(organization_id);
CREATE INDEX idx_chat_messages_user         ON chat_messages(user_id);
CREATE INDEX idx_chat_messages_session      ON chat_messages(organization_id, user_id, created_at DESC);

COMMENT ON TABLE chat_messages IS 'Historial de conversaciones con el agente Semtex.';


-- audit_logs: registro inmutable (solo INSERT).
CREATE TABLE audit_logs (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    action          audit_action NOT NULL,
    description     TEXT,
    metadata        JSONB,
    organization_id UUID         NOT NULL REFERENCES organizations(id),
    performed_by    UUID         REFERENCES users(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_organization ON audit_logs(organization_id);
CREATE INDEX idx_audit_logs_performed_by ON audit_logs(performed_by);
CREATE INDEX idx_audit_logs_action       ON audit_logs(organization_id, action);
CREATE INDEX idx_audit_logs_created_at   ON audit_logs(created_at DESC);

COMMENT ON TABLE audit_logs IS 'Log inmutable de acciones relevantes. Solo INSERT, nunca UPDATE/DELETE.';
