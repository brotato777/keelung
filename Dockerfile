# 1. Build Frontend (React + Vite + Tailwind)
FROM node:20-alpine AS frontend-builder
WORKDIR /frontend

COPY keelung-sights-frontend/package*.json ./
RUN npm install

COPY keelung-sights-frontend/ ./
RUN npm run build

# 2. Build Backend (Spring Boot)
FROM maven:3.9.9-eclipse-temurin-17 AS backend-builder
WORKDIR /backend

COPY keelungapi/pom.xml .
COPY keelungapi/src ./src

COPY --from=frontend-builder /frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

# 3. Runtime Image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=backend-builder /backend/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
