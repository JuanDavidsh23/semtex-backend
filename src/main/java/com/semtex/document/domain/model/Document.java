package com.semtex.document.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio: Document — metadatos de un archivo Excel/CSV subido.
 * El contenido de las filas vive en el contexto financial (FinancialRecord).
 *
 * REGLA HEXAGONAL: Java puro, sin anotaciones externas.
 */
public class Document {

    private final UUID id;
    private final String name;
    private final String storagePath;
    private final String mimeType;
    private final Long fileSizeBytes;
    private final UUID organizationId;
    private final UUID uploadedBy;
    private final LocalDateTime createdAt;

    /** Crea un documento nuevo. */
    public Document(String name, String storagePath, String mimeType,
                    Long fileSizeBytes, UUID organizationId, UUID uploadedBy) {
        this(UUID.randomUUID(), name, storagePath, mimeType, fileSizeBytes,
                organizationId, uploadedBy, LocalDateTime.now());
    }

    /** Reconstruye desde persistencia. */
    public Document(UUID id, String name, String storagePath, String mimeType,
                    Long fileSizeBytes, UUID organizationId, UUID uploadedBy,
                    LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.storagePath = storagePath;
        this.mimeType = mimeType;
        this.fileSizeBytes = fileSizeBytes;
        this.organizationId = organizationId;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
    }

    public boolean isExcel() {
        return mimeType != null && mimeType.contains("spreadsheetml");
    }

    public boolean isCsv() {
        return "text/csv".equals(mimeType);
    }

    public UUID getId()                 { return id; }
    public String getName()             { return name; }
    public String getStoragePath()      { return storagePath; }
    public String getMimeType()         { return mimeType; }
    public Long getFileSizeBytes()      { return fileSizeBytes; }
    public UUID getOrganizationId()     { return organizationId; }
    public UUID getUploadedBy()         { return uploadedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
