# Semtex Backend

Copiloto administrativo/financiero con IA para PyMEs. Backend Spring Boot 3 (Java 21) con
**arquitectura hexagonal modular por bounded context**.

> ✅ Release **1.0.0** — MVP funcional con tenancy por defecto, agente IA con function calling y auditoría AOP.

## Contextos

`shared · identity · document · financial · chat · email · audit`

Cada contexto respeta la regla de dependencia `infrastructure → application → domain`.
El dominio es POJO puro (sin Spring/JPA), verificado con ArchUnit.

## Características 1.0.0

- **Aislamiento multi-inquilino por defecto**: `TenantContext` request-scoped poblado desde el JWT +
  filtro Hibernate global por `organization_id` (no por convención).
- **Seguridad**: Spring Security resource-server con JWT (RS256/JWKS para Supabase, HS256 sólo en tests).
- **Ingesta**: carga de Excel/CSV, original persistido vía `FileStoragePort` (S3/MinIO en dev).
- **IA**: agente sobre LangChain4j + Ollama (`llama3.1:8b`) con `@Tool`
  (`consultarDatosFinancieros`, `compararPeriodos`, `enviarCorreo`), fuera de la transacción y con timeouts.
- **Auditoría**: registro de acciones vía aspecto AOP en el contexto `audit`.
- **Docs**: OpenAPI/Swagger con bearer JWT.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL 14+
- Docker (MinIO para storage en dev; Testcontainers para tests)
- Ollama local con `llama3.1:8b` para el chat IA

## Arranque rápido

```bash
cp application-example.properties application.properties   # completa secretos locales
mvn spring-boot:run
```

API en `http://localhost:8080` · Swagger UI en `http://localhost:8080/swagger-ui.html`.

## Pruebas

```bash
mvn test              # unitarios (Mockito) + reglas de arquitectura (ArchUnit)
mvn verify            # incluye integración con Testcontainers (requiere Docker)
```

## Documentación

- Contrato de API: [`docs/API_CONTRACT.md`](docs/API_CONTRACT.md)
- Auditoría técnica (fase 1) y gaps de arquitectura: [`AUDIT.md`](AUDIT.md)
