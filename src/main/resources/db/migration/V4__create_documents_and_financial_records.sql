-- ============================================================
-- V4: documents (metadatos del archivo) y financial_records (filas en JSONB).
-- ============================================================

CREATE TABLE documents (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(512)  NOT NULL,
    storage_path    VARCHAR(1024) NOT NULL, -- clave del objeto en el bucket (S3/MinIO/Supabase)
    mime_type       VARCHAR(100),
    file_size_bytes BIGINT,
    organization_id UUID          NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    uploaded_by     UUID          NOT NULL REFERENCES users(id),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_documents_organization ON documents(organization_id);
CREATE INDEX idx_documents_uploaded_by  ON documents(uploaded_by);

COMMENT ON TABLE documents IS 'Metadatos de cada archivo Excel/CSV. El binario original vive en el storage; las filas en financial_records.';
COMMENT ON COLUMN documents.storage_path IS 'Clave del objeto en el bucket: org-id/yyyy/uuid-filename.';


-- ============================================================
-- financial_records: cada fila del Excel como objeto JSONB flexible,
-- de modo que cada empresa suba columnas distintas sin tocar el esquema.
-- ============================================================

CREATE TABLE financial_records (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id     UUID        NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    organization_id UUID        NOT NULL REFERENCES organizations(id), -- desnormalizado para filtrar tenant sin JOIN
    sheet_name      VARCHAR(255),
    row_index       INTEGER,
    row_data        JSONB       NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fin_records_document     ON financial_records(document_id);
CREATE INDEX idx_fin_records_organization ON financial_records(organization_id);

-- GIN para consultas por campos DENTRO del JSONB (operador @>).
CREATE INDEX idx_fin_records_row_data_gin ON financial_records USING GIN(row_data);

COMMENT ON TABLE financial_records IS 'Filas extraídas de los Excels/CSVs. JSONB permite columnas dinámicas por empresa.';
COMMENT ON COLUMN financial_records.organization_id IS 'Desnormalizado para que el filtro de tenant de la capa de aplicación (Hibernate filter) no requiera JOIN. El aislamiento NO depende de RLS: con JDBC pooled no aplica.';
