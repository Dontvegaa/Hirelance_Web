# Versión ligeramente mejorada con health check
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

COPY src/ ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=builder /app/target/*.war app.war

# Health check para Render
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8000}/actuator/health || exit 1

EXPOSE ${PORT:-8000}
ENTRYPOINT ["java", "-jar", "app.war"]