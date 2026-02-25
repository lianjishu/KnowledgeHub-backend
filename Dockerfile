FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create directories
RUN mkdir -p /app/uploads /app/logs

# Expose port
EXPOSE 8000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
