# BUILD COMMAND: docker build -t blk-hacking-ind-aman-mishra .

# Stage 1: Build stage
# Selection Criteria: Using Eclipse Temurin 25 JDK on Alpine.
# We install Maven manually to ensure compatibility with Java 25.
FROM eclipse-temurin:25-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
# Selection Criteria: Alpine Linux is chosen for its minimal footprint and security,
# providing the latest Java 25 JRE for high-performance processing.
FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app

# Requirements: Application must run on port 5477
ENV SERVER_PORT=5477
EXPOSE 5477

COPY --from=build /app/target/*.jar app.jar

# Run as non-root user for production security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=5477", "--server.address=0.0.0.0"]