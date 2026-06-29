# ---- Etapa 1: BUILD — compila el .jar con Maven + JDK 21 ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Etapa 2: RUN — solo el JRE + el jar ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/semtex-backend-*.jar app.jar

# Perfil de producción: lee la config de variables de entorno (ver application-prod.properties).
ENV SPRING_PROFILES_ACTIVE=prod
# Ajusta la heap al contenedor (útil en instancias pequeñas, ej. Render Free).
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

# Render inyecta PORT; el perfil prod hace server.port=${PORT:8080}.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
