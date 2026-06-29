# Contrato API Semtex

Base URL local: `http://localhost:8080`

Todas las rutas privadas usan:

```http
Authorization: Bearer <jwt>
Content-Type: application/json
```

El JWT debe incluir estos claims:

```json
{
  "sub": "uuid-del-usuario",
  "email": "user@empresa.com",
  "org_id": "uuid-de-la-organizacion",
  "role": "ADMIN"
}
```

Roles:

- `ADMIN`: gestiona usuarios, documentos, auditoria y configuracion de tenant.
- `OPERATOR`: sube documentos y usa chat.
- `AUDITOR`: lee documentos, registros y auditoria.

## Formato De Error

Todos los errores del `ControllerAdvice` devuelven:

```json
{
  "timestamp": "2026-06-18T16:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "La solicitud contiene campos invalidos.",
  "path": "/api/users",
  "fieldErrors": {
    "email": "El email no es valido"
  }
}
```

## Organizaciones

### Crear organizacion

`POST /api/organizations`

```json
{
  "name": "Ferreteria Lopez",
  "slug": "ferreteria-lopez"
}
```

Respuesta `201`:

```json
{
  "id": "uuid",
  "name": "Ferreteria Lopez",
  "slug": "ferreteria-lopez",
  "active": true,
  "createdAt": "2026-06-18T10:00:00",
  "updatedAt": "2026-06-18T10:00:00"
}
```

### Obtener organizacion del token

`GET /api/organizations`

Respuesta:

```json
[
  {
    "id": "uuid",
    "name": "Ferreteria Lopez",
    "slug": "ferreteria-lopez",
    "active": true
  }
]
```

### Detalle, renombrar y desactivar

- `GET /api/organizations/{id}`
- `PATCH /api/organizations/{id}` con `{ "name": "Nuevo nombre" }`
- `DELETE /api/organizations/{id}`

## Usuarios

### Crear usuario

`POST /api/users`

```json
{
  "email": "operador@empresa.com",
  "role": "OPERATOR",
  "organizationId": "uuid"
}
```

### Listar usuarios por organizacion

`GET /api/users?organizationId={uuid}`

Respuesta:

```json
[
  {
    "id": "uuid",
    "email": "operador@empresa.com",
    "role": "OPERATOR",
    "active": true
  }
]
```

### Gestion

- `GET /api/users/{id}`
- `PATCH /api/users/{id}/role` con `{ "role": "AUDITOR" }`
- `DELETE /api/users/{id}` desactiva el usuario.

## Documentos

### Subir Excel/CSV

`POST /api/documents`

Content type: `multipart/form-data`

Campos:

- `file`: archivo `.csv`, `.xlsx` o `.xls`
- `organizationId`: UUID
- `uploadedByUserId`: UUID

Ejemplo:

```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@balance.csv" \
  -F "organizationId=$ORG_ID" \
  -F "uploadedByUserId=$USER_ID"
```

Respuesta `201`:

```json
{
  "id": "uuid",
  "name": "balance.csv",
  "storagePath": "org-id/balance.csv",
  "mimeType": "text/csv",
  "fileSizeBytes": 12345,
  "organizationId": "uuid",
  "uploadedBy": "uuid",
  "createdAt": "2026-06-18T10:00:00"
}
```

### Listado y detalle

- `GET /api/documents?organizationId={uuid}`
- `GET /api/documents/{id}`
- `DELETE /api/documents/{id}`

Los listados salen ordenados por `createdAt DESC`.

## Registros Financieros

Registros creados automaticamente al subir un documento.

### Listar registros

`GET /api/financial-records?organizationId={uuid}&limit=100`

Filtros opcionales:

- `documentId={uuid}`: filas de un documento validando tenant.
- `fieldName=categoria&value=Ventas`: busqueda exacta por JSONB usando indice GIN.
- `limit=100`: maximo recomendado para UI; el backend limita internamente a `1000`.

Respuesta:

```json
[
  {
    "id": "uuid",
    "documentId": "uuid",
    "sheetName": "default",
    "rowIndex": 1,
    "rowData": {
      "fecha": "2026-06-01",
      "categoria": "Ventas",
      "ingreso": 1000
    },
    "createdAt": "2026-06-18T10:00:00"
  }
]
```

## Chat IA

### Enviar mensaje

`POST /api/chat/messages`

```json
{
  "content": "Cual fue el total de ventas?",
  "organizationId": "uuid",
  "userId": "uuid",
  "documentId": "uuid"
}
```

`documentId` puede omitirse para una conversacion sin contexto financiero.

Respuesta:

```json
{
  "agentResponse": "El total de ventas fue ...",
  "relevantRecords": [
    {
      "id": "uuid",
      "documentId": "uuid",
      "sheetName": "default",
      "rowIndex": 1,
      "rowData": {
        "categoria": "Ventas",
        "ingreso": 1000
      },
      "createdAt": "2026-06-18T10:00:00"
    }
  ]
}
```

El contexto enviado a IA se limita con `semtex.ai.max-context-records`.

### Historial

`GET /api/chat/messages?organizationId={uuid}&userId={uuid}&limit=100`

Filtro opcional:

- `documentId={uuid}`

Respuesta en orden cronologico:

```json
[
  {
    "id": "uuid",
    "role": "USER",
    "content": "Cual fue el total de ventas?",
    "userId": "uuid",
    "documentId": "uuid",
    "tokensUsed": null,
    "createdAt": "2026-06-18T10:00:00"
  }
]
```

## Auditoria

`GET /api/audit/logs?organizationId={uuid}&limit=100`

Respuesta:

```json
[
  {
    "id": "uuid",
    "action": "DOCUMENT_UPLOADED",
    "description": "Documento subido: balance.csv",
    "performedBy": "uuid",
    "createdAt": "2026-06-18T10:00:00"
  }
]
```

## Buenas Practicas Implementadas

- Aislamiento tenant: endpoints validan `organizationId` contra el claim `org_id`.
- Lecturas financieras usan `organizationId + documentId`, no solo `documentId`.
- Listados grandes usan `limit` y tope interno.
- JSONB usa consulta `@>` para aprovechar GIN en busquedas exactas.
- Migracion `V6` agrega indices compuestos para rutas reales de API.
- `application.properties` real queda fuera de git y existe `application-example.properties`.
- Errores son consistentes para frontend.
- CORS configurable con `semtex.cors.allowed-origins`.
