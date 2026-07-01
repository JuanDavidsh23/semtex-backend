-- ============================================================
-- V6: Índices compuestos para las rutas REST y consultas multi-tenant reales.
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_documents_org_created
    ON documents(organization_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_fin_records_org_document_row
    ON financial_records(organization_id, document_id, row_index ASC);

CREATE INDEX IF NOT EXISTS idx_fin_records_org_created
    ON financial_records(organization_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_chat_messages_org_user_created
    ON chat_messages(organization_id, user_id, created_at ASC);

CREATE INDEX IF NOT EXISTS idx_chat_messages_org_user_document_created
    ON chat_messages(organization_id, user_id, document_id, created_at ASC);

CREATE INDEX IF NOT EXISTS idx_audit_logs_org_created
    ON audit_logs(organization_id, created_at DESC);
