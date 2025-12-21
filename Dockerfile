FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="lelouch"
LABEL description="Rubberduck Metier Service Discussion - Spring Boot Application"

# Create app directory
WORKDIR /app

# Copy the JAR file built by Maven
COPY target/*.jar app.jar

# Application port
EXPOSE 8090

# Health check using wget (available in alpine)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8090/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
