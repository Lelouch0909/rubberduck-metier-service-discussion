FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="lelouch"
LABEL description="Rubberduck Metier Service Discussion - Spring Boot Application"

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create app directory
WORKDIR /app

# Copy the JAR file built by Maven
COPY target/*.jar app.jar

# Application port
EXPOSE 8090

# Health check using curl
HEALTHCHECK --interval=15s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8090/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
