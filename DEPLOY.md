# Desplegar el backend en Render (Docker)

El backend se despliega como contenedor Docker. La base de datos y el Auth ya viven en
Supabase, así que con el backend arriba el **login funciona para todo el equipo**.

> Para login NO hacen falta MinIO ni Ollama (esos son solo para documentos y chat).

## Requisitos en el repo (ya incluidos)

- `Dockerfile` — build en 2 etapas (Maven+JDK21 → JRE21).
- `.dockerignore` — evita meter secretos/artefactos locales en la imagen.
- `src/main/resources/application-prod.properties` — perfil `prod`, lee todo de variables
  de entorno. Se activa solo (el Dockerfile pone `SPRING_PROFILES_ACTIVE=prod`).

## Pasos en Render

1. Sube este repo a GitHub.
2. En **render.com** → **New** → **Web Service** → conecta el repo.
3. Render detecta el `Dockerfile` → **Runtime = Docker**.
   - **Build Command** y **Start Command**: déjalos **vacíos** (los maneja el Dockerfile).
4. Plan **Free** sirve para demo (se duerme tras inactividad; arranque frío ~50s).
5. En **Environment**, agrega las variables de abajo.
6. **Create Web Service**. Render construye y te da la URL pública
   (ej. `https://semtex-backend.onrender.com`).
7. (Opcional) **Health Check Path**: `/actuator/health` (ya da 200 porque desactivamos el
   health de mail).

## Variables de entorno a pegar en Render

### Base de datos — usar el SESSION POOLER de Supabase (IPv4)

En Supabase → botón **Connect** → pestaña **Session pooler**. De ahí salen host/usuario.

```
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres.<project-ref>
SPRING_DATASOURCE_PASSWORD=<tu-password-de-supabase>
```

> ⚠️ NO uses `db.<ref>.supabase.co:5432` (conexión directa): desde Render falla por IPv6.

### Seguridad (validación del JWT de Supabase)

```
SUPABASE_JWKS_URI=https://<project-ref>.supabase.co/auth/v1/.well-known/jwks.json
SUPABASE_ISSUER_URI=https://<project-ref>.supabase.co/auth/v1
```

### CORS (dominios del frontend, separados por coma)

```
SEMTEX_CORS_ALLOWED_ORIGINS=https://<tu-front>.vercel.app,http://localhost:3000
```

### (Opcional) Solo si activas documentos/chat

```
SEMTEX_STORAGE_ENDPOINT=...
SEMTEX_STORAGE_ACCESS_KEY=...
SEMTEX_STORAGE_SECRET_KEY=...
SEMTEX_AI_BASE_URL=...
```

## Conectar el frontend

En el front (`.env.local` local o variables en Vercel):

```
NEXT_PUBLIC_API_URL=https://semtex-backend.onrender.com
```

## Verificar que quedó bien

- `https://<tu-backend>.onrender.com/actuator/health` → `{"status":"UP"}`
- `https://<tu-backend>.onrender.com/swagger-ui/index.html` → carga Swagger
- `GET /api/organizations` sin token → **401** (seguridad activa, correcto)
- Login desde el front → debe traer datos (200)

## Notas

- El esquema ya existe en Supabase; Flyway verá que está al día y no hará nada.
- `SPRING_PROFILES_ACTIVE=prod` ya lo pone el Dockerfile; no hace falta agregarlo en Render.
- El `application.properties` local (con secretos) está en `.gitignore` y excluido por
  `.dockerignore`: nunca llega a la imagen.
