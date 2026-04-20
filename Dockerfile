# ── Stage 1: Build the JAR ──────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (caches dependencies layer if pom doesn't change)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ── Stage 2: Run the JAR ─────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy only the built JAR from stage 1
COPY --from=build /app/target/quantity-measurement-app-0.0.1-SNAPSHOT.jar app.jar

# Render sets PORT automatically; Spring picks it up via server.port=${PORT:8080}
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
