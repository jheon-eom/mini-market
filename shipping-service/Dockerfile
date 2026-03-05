# Multi-stage build
FROM gradle:8.5-jdk17 AS builder

WORKDIR /build

# Copy entire project structure
COPY . .

# Build only shipping-service
RUN ./gradlew :shipping-service:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Add non-root user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy jar from builder
COPY --from=builder /build/shipping-service/build/libs/*.jar app.jar

# Environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expose port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8084/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]