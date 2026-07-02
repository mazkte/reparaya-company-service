# ============================================================
# ReparaYa — company-service Dockerfile (Render compatible)
# ============================================================

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

ARG GITHUB_TOKEN

RUN mkdir -p /root/.m2 && \
    echo "<settings><servers><server><id>github</id><username>mazkte</username><password>${GITHUB_TOKEN}</password></server></servers></settings>" \
    > /root/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="mazkte" service="company-service" version="1.0.0"

RUN addgroup -S reparaya && adduser -S reparaya -G reparaya
WORKDIR /app
COPY --from=builder /build/target/reparaya-company-service-*.jar app.jar
USER reparaya

EXPOSE 8083

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8083/actuator/health || exit 1

ENV JAVA_OPTS="-Xms32m -Xmx200m" \
    SPRING_PROFILES_ACTIVE="prod"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]