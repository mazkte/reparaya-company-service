# ============================================================
# ReparaYa — company-service Dockerfile
# Multi-stage build: compila con Maven, ejecuta con JRE ligero
# ============================================================

# ─── STAGE 1: Build ──────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiar pom.xml primero para aprovechar caché de dependencias
COPY pom.xml .

# Descargar dependencias (se cachea si el pom.xml no cambia)
# El settings.xml con el token de GitHub Packages se pasa como secret
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn package -DskipTests -B

# ─── STAGE 2: Runtime ────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="mazkte"
LABEL service="company-service"
LABEL version="1.0.0"

# Usuario no-root por seguridad (OWASP)
RUN addgroup -S reparaya && adduser -S reparaya -G reparaya

WORKDIR /app

# Copiar el jar desde el stage de build
COPY --from=builder /build/target/reparaya-company-service-*.jar app.jar

# Cambiar al user no-root
USER reparaya

# Puerto expuesto
EXPOSE 8080

# Health check — usa el endpoint de Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8083/actuator/health || exit 1

# Variables de entorno con valores por defecto (se sobreescriben en docker-compose)
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE="prod" \
    SERVER_PORT=8080

# Arrancar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
