-- ============================================================
-- V1: Tipos ENUM de PostgreSQL
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE user_role AS ENUM (
    'ADMIN',     -- Control total: usuarios, APIs, archivos
    'OPERATOR',  -- Carga Excels, chat, ordena correos
    'AUDITOR'    -- Solo lectura de reportes e historial
);

CREATE TYPE message_role AS ENUM (
    'USER',   -- Mensaje del usuario humano
    'AGENT'   -- Respuesta generada por Semtex IA
);

CREATE TYPE audit_action AS ENUM (
    'DOCUMENT_UPLOADED',
    'FINANCIAL_QUERY',
    'EMAIL_SENT',
    'EMAIL_FAILED',
    'USER_LOGIN',
    'USER_CREATED',
    'USER_DEACTIVATED',
    'ROLE_CHANGED'
);
