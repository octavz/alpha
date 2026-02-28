# Build stage
FROM gradle:8.10-jdk25-alpine AS build
WORKDIR /app

# Copy Gradle files
COPY gradle gradle
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/alpha-backend.jar app.jar

# Expose port
EXPOSE 3000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]