package com.semtex.document.domain.port.in;

import com.semtex.document.domain.model.Document;

import java.util.List;
import java.util.UUID;

/** Puerto de entrada (driving): consulta y borrado de documentos del tenant. */
public interface QueryDocumentsUseCase {

    List<Document> listByOrganization(UUID organizationId);

    Document get(UUID id);

    void delete(UUID id);
}
