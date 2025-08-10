# Multi-stage build for smaller final image
# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper files and pom.xml first for better caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (this step is cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B -Dmaven.toolchain.skip=true

# Copy the source code
COPY src ./src

# Build the application (skip toolchains plugin for Docker build)
RUN ./mvnw clean package -DskipTests -Dmaven.toolchain.skip=true

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Install curl for health checks (optional)
RUN apk add --no-cache curl

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE $PORT

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Run the application
CMD ["java", "-Dserver.port=${PORT:-8080}", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
