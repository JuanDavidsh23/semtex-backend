# Semtex Backend

Copiloto administrativo/financiero con IA para PyMEs. Backend Spring Boot 3 (Java 21) con
**arquitectura hexagonal modular por bounded context**.

> 🚧 En construcción (MVP 1.0.0). Ver el plan de arranque completo al final del hito de release.

## Contextos

`shared · identity · document · financial · chat · email · audit`

Cada contexto respeta la regla de dependencia `infrastructure → application → domain`.
El dominio es POJO puro (sin Spring/JPA), verificado con ArchUnit.

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
