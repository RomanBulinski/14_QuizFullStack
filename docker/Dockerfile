# Multi-stage build for FullStack Quiz Application

# Stage 1: Build application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY angular-src ./angular-src

# Build the application (includes Angular build via frontend-maven-plugin)
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /build/target/fullstack-quiz-*.jar app.jar

# Expose port
EXPOSE 8002

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8002/api/questions/Spring/10 || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
