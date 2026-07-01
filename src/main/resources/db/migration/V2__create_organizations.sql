-- ============================================================
-- V2: Tabla organizations — raíz del aislamiento multi-inquilino.
-- ============================================================

CREATE TABLE organizations (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_organizations_slug ON organizations(slug);

COMMENT ON TABLE organizations IS 'Entidad raíz del aislamiento multi-inquilino. Cada empresa cliente es una organización.';
COMMENT ON COLUMN organizations.slug IS 'Identificador URL-friendly (ej. "ferreteria-lopez").';
