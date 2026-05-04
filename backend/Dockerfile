FROM gradle:8.10.2-jdk17 AS builder

ARG SERVICE_NAME
WORKDIR /workspace

COPY backend/settings.gradle backend/build.gradle backend/gradle.properties ./
COPY backend/common ./common
COPY backend/services ./services

RUN gradle :${SERVICE_NAME}:bootJar --no-daemon

FROM eclipse-temurin:17-jre

ARG SERVICE_NAME
WORKDIR /app

COPY --from=builder /workspace/services/${SERVICE_NAME}/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
