FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="lelouch"
LABEL description="Rubberduck Metier Service Discussion - Spring Boot Application"

# Install python3 for healthcheck
RUN apk add --no-cache python3

# Create app directory
WORKDIR /app

# Copy the JAR file built by Maven
COPY target/*.jar app.jar

# Application port
EXPOSE 8090

# Health check using python3
HEALTHCHECK --interval=15s --timeout=5s --start-period=40s --retries=3 \
    CMD python3 -c "import urllib.request; urllib.request.urlopen('http://127.0.0.1:8090/actuator/health')" || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
